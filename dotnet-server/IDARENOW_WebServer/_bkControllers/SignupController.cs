using IDARENOW_WebServer.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;

namespace IDARENOW_WebServer.Controllers
{
    public class SignupController : ApiController
    {
        private AlphaRelevant_iDareNowEntities db = new AlphaRelevant_iDareNowEntities();


        // api/Signup/getEmailExist
        [HttpGet]
        public Boolean getEmailExist(string email)
        {
            bool emailexist = db.User_Accounts.Any(x => x.User_Email == email);
            return emailexist;
        }




        // api/Signup/postRegister
        [HttpPost]
        public IHttpActionResult postRegister([FromBody] User_Accounts user_Account)
        {
            try
            {
                
                user_Account.User_Account_Type_ID = 2;
                db.User_Accounts.Add(user_Account);
                user_Account.User_Gender = "Male"; 
                if (user_Account.User_Facebook_ID!="" && user_Account.User_Facebook_ID != null)
                {

                }
                    db.SaveChanges();
                return Ok(true);
            }
            catch(Exception)
            {
                return Ok(false);
            }
            
        }

        [HttpGet]
        public int fbCheckAndSave(string email,string name)
        {
            bool emailexist = db.User_Accounts.Any(x => x.User_Email == email);
            if (emailexist)
            {
                User_Accounts acc= db.User_Accounts.Where(x => x.User_Email == email).Single();
                return acc.User_ID;
            }
            User_Accounts acc_save = new User_Accounts();
            acc_save.User_Email = email;
            acc_save.User_FullName = name;
            acc_save.User_Facebook_ID = "1";
            acc_save.User_Account_Type_ID = 1;
            acc_save.User_Gender = "Male";
            db.User_Accounts.Add(acc_save);
            db.SaveChanges();
            int a  = db.User_Accounts.Where(t => t.User_Email == email).Single().User_ID;

            return a >0?a:-1;
        }



    }
}
