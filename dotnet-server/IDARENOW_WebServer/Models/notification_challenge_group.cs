//------------------------------------------------------------------------------
// <auto-generated>
//     This code was generated from a template.
//
//     Manual changes to this file may cause unexpected behavior in your application.
//     Manual changes to this file will be overwritten if the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

namespace IDARENOW_WebServer.Models
{
    using System;
    using System.Collections.Generic;
    
    public partial class notification_challenge_group
    {
        public int id { get; set; }
        public int challenge_id { get; set; }
        public int n_group_id { get; set; }
    
        public virtual notification_types notification_types { get; set; }
    }
}