using System;
using System.Collections.Generic;
using System.Data;
using System.Data.Entity;
using System.Data.Entity.Infrastructure;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using System.Web.Http.Description;
using System.Data.Entity.Core.Objects;
using IDARENOW_WebServer.Models;

namespace IDARENOW_WebServer.Controllers
{
    public class User_AccountsController : ApiController
    {
        private AlphaRelevant_iDareNowEntities db = new AlphaRelevant_iDareNowEntities();

        // GET: api/User_Accounts
        [HttpGet]
        public IQueryable<User_Accounts> GetUser_Accounts()
        {
            return db.User_Accounts;
        }
        [HttpGet]
        // GET: api/User_Accounts/5
        [ResponseType(typeof(User_Accounts))]
        public IHttpActionResult GetUser_Accounts(int id)
        {
            User_Accounts user_Accounts = db.User_Accounts.Find(id);
            if (user_Accounts == null)
            {
                return NotFound();
            }
            user_Accounts.User_Profile_Picture = Constants.ip_port_conn + "/Images/icons/" + user_Accounts.User_Profile_Picture;
            int total_complete_challenges = db.Assign_Challenge.Where(x => x.Challenge_To_User_ID == id && x.Challenge_Approval_Status == 4).Count();
            int total_challenges = db.Assign_Challenge.Where(x => x.Challenge_To_User_ID == id).Count();
            int approved_challenges = db.Assign_Challenge.Where(x => x.Challenge_To_User_ID == id && (x.Challenge_Approval_Status == 2 || x.Challenge_Approval_Status == 4)).Count();
            int uncompleted_chalenges = db.Assign_Challenge.Where(x => x.Challenge_To_User_ID == id && System.Data.Entity.DbFunctions.AddDays(x.Challenge_Created_Date, (Int32?)x.Challenge_Duration) < DateTime.Now && x.Challenge_Approval_Status != 4 && x.Challenge_Approval_Status != 3).Count();
            if (total_challenges > 0)
            {
                user_Accounts.User_Challenged_Completed = (int)(((double) total_complete_challenges / total_challenges) * 100);
                user_Accounts.User_Approval_Rate = (int)(((double)approved_challenges / total_challenges) * 100);
                user_Accounts.User_Total_Earnings = (int)(((double)uncompleted_chalenges / total_challenges) * 100);
            }
            else
            {
                user_Accounts.User_Challenged_Completed = 0;
                user_Accounts.User_Approval_Rate = 0;
                user_Accounts.User_Total_Earnings = 0;
            }

            var count = from statuses in db.Post_approve_reject
                        join vid in db.Videos on statuses.v_id equals vid.Video_ID
                        where vid.Video_to_user_id == user_Accounts.User_ID
                        select new
                        {
                            statuses = statuses.status
                        };
            int approve_counter = 0;
            int reject_counter = 0;
            foreach(var item in count)
            {
                if (item.statuses == 1)
                    approve_counter++;
                else
                    reject_counter++;
            }

            Decimal rating;
            if (approve_counter > reject_counter)
            {
                rating = ((Decimal)(approve_counter) / (approve_counter + reject_counter)) * 100;
                rating = rating % 5;
            }
            else rating = 0;
            rating = Math.Round(rating, 1);
            user_Accounts.User_Ratings = rating;

           
            return Ok(user_Accounts);
        }

        // post : api/updateUser_Accounts/5
        [ResponseType(typeof(void))]
        public IHttpActionResult updateUser_Accounts(int id, User_Accounts user_Accounts)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            if (id != user_Accounts.User_ID)
            {
                return BadRequest();
            }

            db.Entry(user_Accounts).State = EntityState.Modified;

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!User_AccountsExists(id))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return StatusCode(HttpStatusCode.NoContent);
        }

        // POST: api/User_Accounts
        [ResponseType(typeof(User_Accounts))]
        public IHttpActionResult PostUser_Accounts(User_Accounts user_Accounts)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            db.User_Accounts.Add(user_Accounts);
            db.SaveChanges();

            return CreatedAtRoute("DefaultApi", new { id = user_Accounts.User_ID }, user_Accounts);
        }

        // DELETE: api/User_Accounts/5
        [ResponseType(typeof(User_Accounts))]
        public IHttpActionResult DeleteUser_Accounts(int id)
        {
            User_Accounts user_Accounts = db.User_Accounts.Find(id);
            if (user_Accounts == null)
            {
                return NotFound();
            }

            db.User_Accounts.Remove(user_Accounts);
            db.SaveChanges();

            return Ok(user_Accounts);
        }

        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                db.Dispose();
            }
            base.Dispose(disposing);
        }
        [HttpGet]
        public int getUserIdByEmail(string email)
        {
            User_Accounts user = db.User_Accounts.SingleOrDefault(x => x.User_Email == email);
            if (user != null && user.User_ID >0)
            {
               int ua = user.User_ID;
                if (ua > 0)
                {
                    return ua;
                }
            }
            return 0;
        }
        private bool User_AccountsExists(int id)
        {
            return db.User_Accounts.Count(e => e.User_ID == id) > 0;
        }
        [HttpGet]
        public IHttpActionResult User_AccountsExistsviaEmail(string email)
        {
            if (email != null || email != "")
            {
                bool a = db.User_Accounts.Count(e => e.User_Email == email) > 0;
                return Ok(a);
            }
            return Ok(false);
        }
        [HttpGet]
        public IHttpActionResult User_AccountsViaNameSearch(string name)
        {
            if (name != null || name != "")
            {
                List<User_Accounts> a = db.User_Accounts.Where(x=>x.User_FullName.Contains(name)).ToList();
                if(a.Count>0)
                    return Ok(a);
                return Ok(false);
            }
            return Ok(false);
        }
    }
}