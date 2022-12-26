using IDARENOW_WebServer.Models;
using System.Collections.Generic;
using System.Linq;
using System.Web.Http;

namespace IDARENOW_WebServer.Controllers
{
    public class NotificationController : ApiController
    {
        private AlphaRelevant_iDareNowEntities db = new AlphaRelevant_iDareNowEntities();
        // GET: Notification

        [HttpGet]
        public IHttpActionResult getAllNotifications(int user_id,int offset)
        {

            //get those challenges for notification fragment that are accepted ,rejected,or not viewed(challenged)
            notificationData[] arra = (from challege in db.Assign_Challenge
                                       
                                       join acc in db.User_Accounts
                                       on challege.Challenge_From_ID equals acc.User_ID

                                       where challege.Challenge_To_User_ID == user_id
                                       //where (challege.Challenge_Approval_Status == Constants.accepted_challenges || challege.Challenge_Approval_Status == Constants.initial_challenge || challege.Challenge_Approval_Status == Constants.rejected_challenges)
                                       
                                       select new notificationData
                                       {
                                           challege_id = challege.Challenge_ID,
                                           to_id = challege.Challenge_To_User_ID,
                                           from_id = challege.Challenge_From_ID,
                                           from_name = acc.User_FullName,
                                           title = challege.Challenge_Tittle,
                                           description = challege.Challenge_Description,
                                           profile_image = "",
                                           approval_status = challege.Challenge_Approval_Status
                                       }).OrderByDescending(c=>c.challege_id).Skip(offset).Take(3).ToArray();
            //getProfileImages
            foreach(notificationData item in arra)
            {
                item.profile_image = Constants.ip_port_conn + "/Images/icons/" + db.User_Accounts.Find(item.from_id).User_Profile_Picture;
                int isNUll = db.notification_challenge_group.Where(x => x.challenge_id == item.challege_id).OrderByDescending(t => t.id).Take(1).Select(r => r.n_group_id).FirstOrDefault();
                item.notification_id = isNUll;
                
            }
            return Ok(arra.Where(x=>x != null));
        }



        [HttpGet]
        public IHttpActionResult checkPendingAssignChallenges(int user_id,int count)
        {
            notificationData[] arra = (from challege in db.Assign_Challenge

                                       join notifi_group in db.notification_challenge_group
                                       on challege.Challenge_ID equals notifi_group.challenge_id

                                       join notifi_type in db.notification_types
                                       on notifi_group.n_group_id equals notifi_type.n_type_id

                                       where notifi_type.sub_n_type_id == Constants.initial_challenge
                                       where notifi_type.notification_shown_status == 0
                                       where challege.Challenge_To_User_ID == user_id
                                       select new notificationData
                                       {
                                           challege_id = challege.Challenge_ID,
                                           notification_id = notifi_group.n_group_id,
                                           to_id = challege.Challenge_To_User_ID,
                                           from_id = challege.Challenge_From_ID,
                                           title = challege.Challenge_Tittle,
                                           description = challege.Challenge_Description,
                                           from_name = db.User_Accounts.Where(x => x.User_ID == user_id).Select(y => y.User_FullName).FirstOrDefault()
                                       }).ToArray();
            foreach (notificationData item in arra)
            {
                item.profile_image = Url.Content("~/Images\\" + db.User_Accounts.Find(item.from_id).User_Profile_Picture);
            }

            foreach (var item in arra)
            {
                notification_challenge_group s = db.notification_challenge_group.Where(x => x.n_group_id== item.notification_id).SingleOrDefault();
                s.notification_types.notification_shown_status = 1;
            }
            db.SaveChanges();
            return Ok(arra.Take(count));
        }
        [HttpGet]
        public IHttpActionResult checkPendingAcceptChallenge(int user_id)
        {

            Assign_Challenge[] cha = db.Assign_Challenge.Where(x => x.Challenge_From_ID == user_id).ToArray();
            List<notificationData> acceptedChallenges = new List<notificationData>();
            foreach (Assign_Challenge challenge in cha)
            {
                notification_challenge_group[] arr = db.notification_challenge_group.Where(x => x.challenge_id == challenge.Challenge_ID).ToArray();
                foreach (notification_challenge_group item in arr)
                {
                    notification_types n = db.notification_types.Where(x => x.n_type_id == item.n_group_id).Where(y => y.sub_n_type_id == Constants.accepted_challenges && y.notification_shown_status == 0).SingleOrDefault();
                    if (n != null)
                    {   
                        n.notification_shown_status = 1;
                        notificationData obj = new notificationData();
                        obj.title = "Challenge Accepted";
                        obj.description = challenge.Challenge_Tittle; 
                        obj.challege_id = challenge.Challenge_ID;
                        obj.from_id = challenge.Challenge_From_ID;
                        obj.to_id = challenge.Challenge_To_User_ID;
                        obj.notification_id = item.id;
                        acceptedChallenges.Add(obj);
                    }
                }
            }
            if (acceptedChallenges.Count > 0)
            {
                foreach (var item in acceptedChallenges)
                {
                   Assign_Challenge challenge = db.Assign_Challenge.Where(x => x.Challenge_ID == item.challege_id).SingleOrDefault();
                    challenge.Challenge_Created_Date = System.DateTime.Now;
                }
            }
            
            db.SaveChanges();
            return Ok(acceptedChallenges.ToArray());
        }


    }
    class notificationData
    {
        
        public int challege_id;
        public int? notification_id;
        public int? to_id;
        public int from_id;
        public string from_name;
        public string title;
        public string description;
        public string profile_image;
        public int? approval_status;
    }
    
}