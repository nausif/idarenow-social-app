using IDARENOW_WebServer.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Threading.Tasks;
using System.Web.Http;

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
                    rating = e.User_Ratings
                }).Take<UserResult>(5).ToArray();

            foreach (UserResult item in query)
            {
                item.profilePic = Url.Content("~/Images\\" + item.profilePic);
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
        public decimal? rating;
    }
}
