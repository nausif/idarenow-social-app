using IDARENOW_WebServer.Models;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Net.Mail;
using System.Web.Http;

namespace IDARENOW_WebServer.Controllers
{
    public class LoginController : ApiController
    {
        private AlphaRelevant_iDareNowEntities db = new AlphaRelevant_iDareNowEntities();
        
        [HttpGet]
        // api/Login/GetAuthentication
        public int GetAuthentication(string email,string pass)
        {

            User_Accounts ua = db.User_Accounts.Where(e => e.User_Password.Equals(pass)
                                            && e.User_Email.Equals(email)).FirstOrDefault();
            if (ua != null)
                {
                   return ua.User_ID;
                }
                return 0;
            
        }

        [HttpGet]
        public IHttpActionResult getUserDetails(int user_id)
        {

            User_Accounts ua = db.User_Accounts.Where(e => e.User_ID.Equals(user_id)).FirstOrDefault();
            if (ua != null)
            {
                UserDetails ud = new UserDetails();
                ud.user_id = ua.User_ID;
                ud.user_name = ua.User_FullName;
                if (ua.User_Profile_Picture != null)
                    ud.profile_pic = Constants.ip_port_conn + "/Images/icons/" + ua.User_Profile_Picture;
                return Ok(ud);
            }
            return NotFound();
        }

    

    [HttpGet]
    public Boolean getForgetPassword(string email)
    {
        User_Accounts ua = db.User_Accounts.Where(e => e.User_Email.Equals(email)).FirstOrDefault();
            if (ua != null)
            {
                var fromAddress = new MailAddress("qualitydesigns14@gmail.com", "iDareNow");
                var toAddress = new MailAddress(email, ua.User_FullName);
                const string fromPassword = "designer098";
                const string subject = "iDareNow Account Password";
                string body = "Your account pass is \"" + ua.User_Password + "\"";

                var smtp = new SmtpClient
                {
                    Host = "smtp.gmail.com",
                    Port = 587,
                    EnableSsl = true,
                    DeliveryMethod = SmtpDeliveryMethod.Network,
                    UseDefaultCredentials = false,
                    Credentials = new NetworkCredential(fromAddress.Address, fromPassword)
                };
                using (var message = new MailMessage(fromAddress, toAddress)
                {
                    Subject = subject,
                    Body = body
                })
                {
                    smtp.Send(message);
                }
                return true;
            }
            else
            {
                return false;
            }
    }

 }
    public class UserDetails
    {
        public int user_id;
        public String user_name;
        public String profile_pic;
    }
}
