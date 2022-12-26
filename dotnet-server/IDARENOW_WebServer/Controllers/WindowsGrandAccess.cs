﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.InteropServices;
using System.Security.AccessControl;
using System.Security.Principal;
using System.Web;

namespace IDARENOW_WebServer.Controllers
{
    public class WindowsGrandAccess : IDisposable
    {
        #region DLL-Import
        // All the code to manipulate a security object is available in .NET framework,
        // but its API tries to be type-safe and handle-safe, enforcing a special implementation
        // (to an otherwise generic WinAPI) for each handle type. This is to make sure
        // only a correct set of permissions can be set for corresponding object types and
        // mainly that handles do not leak.
        // Hence the AccessRule and the NativeObjectSecurity classes are abstract.
        // This is the simplest possible implementation that yet allows us to make use
        // of the existing .NET implementation, sparing necessity to
        // P/Invoke the underlying WinAPI.

        [DllImport("user32.dll", SetLastError = true)]
        private static extern IntPtr GetProcessWindowStation();

        [DllImport("user32.dll", SetLastError = true)]
        private static extern IntPtr GetThreadDesktop(int dwThreadId);

        [DllImport("kernel32.dll", SetLastError = true)]
        private static extern int GetCurrentThreadId();
        #endregion

        #region Variables && Properties
        public static int WindowStationAllAccess { get; private set; } = 0x000f037f;
        public static int DesktopRightsAllAccess { get; private set; } = 0x000f01ff;

        private GenericSecurity WindowStationSecurity { get; set; }
        private GenericSecurity DesktopSecurity { get; set; }
        private int? OldWindowStationMask { get; set; }
        private int? OldDesktopMask { get; set; }
        private NTAccount AccountInfo { get; set; }
        private SafeHandle WsSafeHandle { get; set; }
        private SafeHandle DSafeHandle { get; set; }
        #endregion

        #region Constructor & Dispose
        public WindowsGrandAccess(NTAccount accountInfo, int windowStationMask, int desktopMask)
        {
            if (accountInfo != null)
                Init(accountInfo, windowStationMask, desktopMask);
        }

        public void Dispose()
        {
            try
            {
                if (AccountInfo == null)
                    return;

                RestAccessMask(OldWindowStationMask, WindowStationAllAccess, WindowStationSecurity, WsSafeHandle);
                RestAccessMask(OldDesktopMask, DesktopRightsAllAccess, DesktopSecurity, DSafeHandle);
            }
            catch (Exception ex)
            {
                throw new Exception($"The object \"{nameof(WindowsGrandAccess)}\" could not be dispose.", ex);
            }
        }
        #endregion

        #region Methods
        private void Init(NTAccount accountInfo, int windowStationMask, int desktopMask)
        {
            AccountInfo = accountInfo;

            WsSafeHandle = new NoopSafeHandle(GetProcessWindowStation());
            WindowStationSecurity = new GenericSecurity(false, ResourceType.WindowObject, WsSafeHandle, AccessControlSections.Access);

            DSafeHandle = new NoopSafeHandle(GetThreadDesktop(GetCurrentThreadId()));
            DesktopSecurity = new GenericSecurity(false, ResourceType.WindowObject, DSafeHandle, AccessControlSections.Access);

            OldWindowStationMask = ReadAccessMask(WindowStationSecurity, WsSafeHandle, windowStationMask);
            OldDesktopMask = ReadAccessMask(DesktopSecurity, DSafeHandle, desktopMask);
        }

        private AuthorizationRuleCollection GetAccessRules(GenericSecurity security)
        {
            return security.GetAccessRules(true, false, typeof(NTAccount));
        }

        private int? ReadAccessMask(GenericSecurity security, SafeHandle safeHandle, int accessMask)
        {
            var ruels = GetAccessRules(security);

            var username = AccountInfo.Value;
            if (!username.Contains("\\"))
                username = $"{Environment.MachineName}\\{username}";

            var userResult = ruels.Cast<GrantAccessRule>().SingleOrDefault(r => r.IdentityReference.Value.ToLower() == username.ToLower() && accessMask == r.PublicAccessMask);
            if (userResult == null)
            {
                AddGrandAccess(security, accessMask, safeHandle);
                userResult = ruels.Cast<GrantAccessRule>().SingleOrDefault(r => r.IdentityReference.Value.ToLower() == username.ToLower());
                if (userResult != null)
                    return userResult.PublicAccessMask;
            }
            else
                return userResult.PublicAccessMask;

            return null;
        }

        private void AddGrandAccess(GenericSecurity security, int accessMask, SafeHandle safeHandle)
        {
            var rule = new GrantAccessRule(AccountInfo, accessMask, AccessControlType.Allow);
            security.AddAccessRule(rule);
            security.Persist(safeHandle, AccessControlSections.Access);
        }

        private void RemoveGrantAccess(GenericSecurity security, int accessMask, SafeHandle safeHandle)
        {
            var rule = new GrantAccessRule(AccountInfo, accessMask, AccessControlType.Allow);
            security.RemoveAccessRule(rule);
            security.Persist(safeHandle, AccessControlSections.Access);
        }

        private void SetGrandAccess(GenericSecurity security, int accessMask, SafeHandle safeHandle)
        {
            var rule = new GrantAccessRule(AccountInfo, accessMask, AccessControlType.Allow);
            security.SetAccessRule(rule);
            security.Persist(safeHandle, AccessControlSections.Access);
        }

        private void RestAccessMask(int? oldAccessMask, int fullAccessMask, GenericSecurity security, SafeHandle safeHandle)
        {
            if (oldAccessMask == null)
                RemoveGrantAccess(security, fullAccessMask, safeHandle);
            else if (oldAccessMask != fullAccessMask)
            {
                SetGrandAccess(security, oldAccessMask.Value, safeHandle);
            }
        }
        #endregion

        #region private classes
        private class GenericSecurity : NativeObjectSecurity
        {
            public GenericSecurity(
                bool isContainer, ResourceType resType, SafeHandle objectHandle,
                AccessControlSections sectionsRequested)
                : base(isContainer, resType, objectHandle, sectionsRequested) { }

            new public void Persist(SafeHandle handle, AccessControlSections includeSections)
            {
                base.Persist(handle, includeSections);
            }

            new public void AddAccessRule(AccessRule rule)
            {
                base.AddAccessRule(rule);
            }

            new public bool RemoveAccessRule(AccessRule rule)
            {
                return base.RemoveAccessRule(rule);
            }

            new public void SetAccessRule(AccessRule rule)
            {
                base.SetAccessRule(rule);
            }

            new public AuthorizationRuleCollection GetAccessRules(bool includeExplicit, bool includeInherited, Type targetType)
            {
                return base.GetAccessRules(includeExplicit, includeInherited, targetType);
            }

            public override Type AccessRightType
            {
                get { throw new NotImplementedException(); }
            }

            public override AccessRule AccessRuleFactory(
                System.Security.Principal.IdentityReference identityReference,
                int accessMask, bool isInherited, InheritanceFlags inheritanceFlags,
                PropagationFlags propagationFlags, AccessControlType type)
            {
                return new GrantAccessRule(identityReference, accessMask, isInherited, inheritanceFlags, propagationFlags, type);
            }

            public override Type AccessRuleType
            {
                get { return typeof(AccessRule); }
            }

            public override AuditRule AuditRuleFactory(
                System.Security.Principal.IdentityReference identityReference, int accessMask,
                bool isInherited, InheritanceFlags inheritanceFlags,
                PropagationFlags propagationFlags, AuditFlags flags)
            {
                throw new NotImplementedException();
            }

            public override Type AuditRuleType
            {
                get { return typeof(AuditRule); }
            }
        }

        private class GrantAccessRule : AccessRule
        {
            public GrantAccessRule(IdentityReference identity, int accessMask, bool isInherited,
                                     InheritanceFlags inheritanceFlags, PropagationFlags propagationFlags,
                                     AccessControlType type) :
                                     base(identity, accessMask, isInherited,
                                          inheritanceFlags, propagationFlags, type)
            { }

            public GrantAccessRule(IdentityReference identity, int accessMask, AccessControlType type) :
                base(identity, accessMask, false, InheritanceFlags.None,
                     PropagationFlags.None, type)
            { }

            public int PublicAccessMask
            {
                get { return base.AccessMask; }
            }
        }

        // Handles returned by GetProcessWindowStation and GetThreadDesktop should not be closed
        private class NoopSafeHandle : SafeHandle
        {
            public NoopSafeHandle(IntPtr handle) :
                base(handle, false)
            { }

            public override bool IsInvalid
            {
                get { return false; }
            }

            protected override bool ReleaseHandle()
            {
                return true;
            }
        }
        #endregion
    }
}