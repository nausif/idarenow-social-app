using IDARENOW_WebServer.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using System.Web.Http;

namespace IDARENOW_WebServer.Controllers
{
    public class NotificationController : ApiController
    {
        private AlphaRelevant_iDareNowEntities db = new AlphaRelevant_iDareNowEntities();
        // GET: Notification
        [HttpGet]
        public IHttpActionResult checkPendingNotification(int id)
        {
            User_AccountsController ua = new User_AccountsController();
            notificationData[] arra = (from challege in db.Assign_Challenge
                        join notifi in db.Notifications
                        on challege.Challenge_notification_id equals notifi.notification_id
                        where challege.Challenge_To_User_ID == id
                        where notifi.status == 0
                        select new notificationData
                        {
                            challege_id = challege.Challenge_ID,
                            notification_id = challege.Challenge_notification_id,
                            challenger_id = challege.Challenge_ID,
                            from_id = challege.Challenge_From_ID,
                            title = challege.Challenge_Tittle,
                            description = challege.Challenge_Description,
                            profile_image = ""
                        }).ToArray();
            foreach (notificationData item in arra)
            {
                  item.profile_image = Url.Content("~/Images\\" + db.User_Accounts.Find(item.from_id).User_Profile_Picture);
            }
            foreach (var item in arra)
            {
                Notification s = db.Notifications.Where(x => x.notification_id == item.notification_id).SingleOrDefault();
                s.status = 1;
                db.SaveChanges();
            }
            
            return Ok(arra);
        }
        

    }
    class notificationData
    {
        public int? notification_id;
        public int challenger_id;
        public int from_id;
        public string title;
        public string description;
        public string profile_image;
        public int challege_id;
    }
}