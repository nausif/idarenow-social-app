using IDARENOW_WebServer.Models;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
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


        


    }
}
