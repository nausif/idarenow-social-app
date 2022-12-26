using IDARENOW_WebServer.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Threading.Tasks;
using System.Web.Http;
using static IDARENOW_WebServer.Controllers.ChallengesSummaryController;

namespace IDARENOW_WebServer.Controllers
{
    public class Search_UserController : ApiController
    {
        private AlphaRelevant_iDareNowEntities db = new AlphaRelevant_iDareNowEntities();
        
        [HttpGet]
        public  IHttpActionResult GetSearchUsers(string search,int id)
        {

            ProfileController pc = new ProfileController();
            UserResult[] query = db.User_Accounts.Where(e => e.User_FullName.Contains(search)).Where(t=>t.User_ID!=id).Select(
                e => new UserResult(){
                    id = e.User_ID,
                    fullName = e.User_FullName,
                    profilePic = e.User_Profile_Picture,
                    email = e.User_Email
                }).Take<UserResult>(3).ToArray();

            foreach (UserResult item in query)
            {

                item.profilePic = Constants.ip_port_conn + "/Images/icons/" + item.profilePic;
            }
            if (query == null)
                return NotFound();
            else
                return Ok(query);
        }


    }
    class UserResult
    {
        public int id;
        public string fullName;
        public string profilePic;
        public string email;
    }
}
