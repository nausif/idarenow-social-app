using System;
using System.Linq;
using System.Web.Http;
using System.Net.Http;
using System.Web;
using System.Collections.Generic;
using System.Net;

using System.Threading.Tasks;
using System.Data.Entity.Core.Objects;
using IDARENOW_WebServer.Models;

namespace IDARENOW_WebServer.Controllers
{
    public class ChallengeController : ApiController
    {
        private AlphaRelevant_iDareNowEntities db = new AlphaRelevant_iDareNowEntities();


        [HttpGet]
        public IHttpActionResult getUserTotalBalance(int user_id)
        {

            if (user_id > 0)
            {
                int amount = (int)db.User_Accounts.Where(u => u.User_ID == user_id).Select(x => x.User_Balance_Total_Amount).FirstOrDefault();
                return Ok(amount);
            }
            else
            {
                return BadRequest();
            }
        }

        [HttpGet]
        public IHttpActionResult getChallengeIdByType(String type)
        {
            
            if (type != "")
            {
                Challenge_Type a = db.Challenge_Type.Where(x => x.Challenge_Type_Description == type).FirstOrDefault();
                if(a.Challenge_Type_ID>0)
                    return Ok(a.Challenge_Type_ID);
                return BadRequest();
            } else{
                return BadRequest();
            }
        }
        [HttpPost]
        public IHttpActionResult setChallengeFromUser([FromBody] ChallengeFromUser type)
        {
            try
            {
                
                Assign_Challenge from = new Assign_Challenge();
                from.Challenge_Type_ID = type.challengeType;
                from.Challenge_From_ID = type.challengeFrom;
                from.Challenge_To_User_ID = type.challengeTo;
                from.Challenge_Description = type.des;
                from.Challenge_Created_Date = DateTime.Now;
                from.Challenge_Tittle = type.title;
                from.Challenge_Approval_Status = 1;
                db.Assign_Challenge.Add(from);
                db.SaveChanges();
                BaseClass.assignChallengeNotification(from.Challenge_ID);

                return Ok(true);
            }
            catch(Exception e)
            {
                return Ok(false);
            }

        }
        [HttpGet]
        public IHttpActionResult getChallengeById(int id)
        {
            if (id > 0)
            {
                ChallengeDetails cd = (from challenge in db.Assign_Challenge
                                       where challenge.Challenge_ID == id
                                       join acc in db.User_Accounts
                                       on challenge.Challenge_From_ID equals acc.User_ID
                                       select new ChallengeDetails
                                       {
                                           challenge_id = challenge.Challenge_ID,
                                           challenge_Type_ID = challenge.Challenge_Type_ID,
                                           challenge_From_ID = challenge.Challenge_From_ID,
                                           challenge_To_User_ID = challenge.Challenge_To_User_ID,
                                           challenge_Approval_Status = challenge.Challenge_Approval_Status,
                                           challenge_Description = challenge.Challenge_Description,
                                           challenge_Created_Date = challenge.Challenge_Created_Date,
                                           challenge_Duration = challenge.Challenge_Duration,
                                           challenge_Prize = challenge.Challenge_Amount,
                                           challenge_Status = challenge.Challenge_Status,
                                           challenge_Tittle = challenge.Challenge_Tittle,
                                           challenge_from_profile_name = acc.User_FullName,
                                           challenge_from_profile_image = Constants.ip_port_conn + "/Images/icons/" + acc.User_Profile_Picture
                                       }).SingleOrDefault();
                    cd.challenge_Expiry_Date = convertDateTimeIntoMillis(cd.challenge_Created_Date, cd.challenge_Duration);
                    return Ok(cd);
            }
            else
            {
                return BadRequest();
            }
        }

        public int convertDateTimeIntoMillis(DateTime date1,int duration)
        {   
            DateTime dt = date1.AddDays(duration);
            TimeSpan ts = dt - DateTime.Now;
            return (int) ts.TotalMilliseconds;
        }

        [HttpGet]
        public IHttpActionResult ChallengeStatusUpdate(int c_id,int a_r_challenge)
        {
            Assign_Challenge a =db.Assign_Challenge.Where(x => x.Challenge_ID== c_id).SingleOrDefault();
            a.Challenge_Approval_Status = a_r_challenge;
            db.SaveChanges();


            notification_types n_type = new notification_types();
            n_type.notification_category = Constants.ChallengeString;

            if (a_r_challenge ==1)
                n_type.sub_n_type_id = Constants.accepted_challenges;
            else if(a_r_challenge == 0)
                n_type.sub_n_type_id = Constants.rejected_challenges;

            n_type.notification_shown_status = 0;
            db.notification_types.Add(n_type);
            db.SaveChanges();

            notification_challenge_group n_group = new notification_challenge_group();
            n_group.challenge_id = c_id;
            n_group.n_group_id = n_type.n_type_id;
            db.notification_challenge_group.Add(n_group);
            db.SaveChanges();

            return Ok(n_group);
        }

        // Challenge Associated Videos

        public IHttpActionResult getChallengeAssociatedVideos(int user_id,int challenge_id, int offset)
        {
            NewsfeedPost[] challenges = (from v in BaseClass.db.Videos
                                         where v.Challenge_id == challenge_id
                                         join challenge in BaseClass.db.Assign_Challenge
                                         on v.Challenge_id equals challenge.Challenge_ID
                                         select new NewsfeedPost
                                         {
                                             challenge_id = challenge.Challenge_ID,
                                             Challenge_From_Name = BaseClass.db.User_Accounts.Where(x => x.User_ID == challenge.Challenge_From_ID).Select(y => y.User_FullName).FirstOrDefault(),
                                             c_description = challenge.Challenge_Description,
                                             c_title = challenge.Challenge_Tittle,
                                             video_dateTime = v.Video_Upload_Date,
                                             challenge_dateTime = challenge.Challenge_Created_Date,
                                             challenge_Duration = challenge.Challenge_Duration,
                                             challenge_type_id = challenge.Challenge_Type_ID,
                                             video_id = v.Video_ID,
                                             challenge_from_id = challenge.Challenge_From_ID,
                                             winner_ID = challenge.Challenge_Winner_ID,
                                             post_approve_reject = BaseClass.db.Post_approve_reject.Where(x => x.v_id == v.Video_ID).Where(y => y.user_id == user_id).Select(item => item.status).FirstOrDefault()
                                         }).ToList().OrderByDescending(c => c.challenge_id).Skip(offset).Take(3).ToArray();
            for (int i = 0; i < challenges.Length; i++)
            {
                challenges[i].dateTime = Constants.parseDate(challenges[i].video_dateTime.Ticks);
                challenges[i].challenge_Expiry_Date = convertDateTimeIntoMillis(challenges[i].challenge_dateTime, challenges[i].challenge_Duration);
                int t = challenges[i].video_id;
                challenges[i].approve_ratio = db.Post_approve_reject.Where(x => x.v_id == t).Where(y => y.status == 1).Count();
                challenges[i].reject_ratio = db.Post_approve_reject.Where(x => x.v_id == t).Where(y => y.status == 2).Count();
            }

            foreach (var item in challenges)
            {
                if (item.challenge_type_id == 1)
                {
                    item.Challenge_To_User_ID = BaseClass.db.Assign_Challenge.Where(x => x.Challenge_ID == item.challenge_id).Select(y => y.Challenge_To_User_ID).FirstOrDefault();
                    item.Challenge_To_Name = BaseClass.db.User_Accounts.Where(x => x.User_ID == item.Challenge_To_User_ID).Select(y => y.User_FullName).FirstOrDefault();
                }
                else
                {
                    item.Challenge_To_User_ID = BaseClass.db.Videos.Where(x => x.Video_ID == item.video_id).Select(y => y.Video_to_user_id).FirstOrDefault();
                    item.Challenge_To_Name = BaseClass.db.User_Accounts.Where(x => x.User_ID == item.Challenge_To_User_ID).Select(y => y.User_FullName).FirstOrDefault();
                }

                item.challenge_to_img = Constants.ip_port_conn + "/Images/icons/" + BaseClass.db.User_Accounts.Where(x => x.User_ID == item.Challenge_To_User_ID).Select(y => y.User_Profile_Picture).FirstOrDefault();
                item.video = Constants.ip_port_conn + "/ChallengeVideo/" + item.challenge_id + "_folder/inputVideo/" + BaseClass.db.Videos.Where(x => x.Video_ID == item.video_id).Select(y => y.Video_name).FirstOrDefault();

                //if (item.video_id > 0)
                //{
                //    Video_comments[] comm = BaseClass.db.Video_comments.Where(x => x.Video_ID == item.video_id).ToArray();
                //    item.comments = (from comment in comm select new VideoComment {user_id = comment.User_ID, _comment =comment.Comment_text ,name = BaseClass.db.User_Accounts.Where(x=>x.User_ID == comment.User_ID).Select(x=>x.User_FullName).FirstOrDefault() }).ToArray();
                //}
            }

            return Ok(challenges);
        }


    }
  public  class ChallengeFromUser
    {
        public string des { get; set; }
        public string title { get; set; }
        public Nullable<int> durationTime { get; set; }
        public int challengeType { get; set; }
        public Nullable<int> challengeStatus { get; set; }
        public int challengeFrom { get; set; }
        public int challengeTo { get; set; }
    }

    public class ChallengeDetails
    {
        public int challenge_id;
        public int challenge_Type_ID;
        public int challenge_From_ID;
        public int? challenge_To_User_ID;
        public int? challenge_Approval_Status;
        public string challenge_Description;
        public string challenge_from_profile_name;
        public string challenge_from_profile_image;
        public int challenge_Expiry_Date;
        public Double? challenge_Prize;
        public DateTime challenge_Created_Date;
        public int challenge_Duration;
        public int? challenge_Status;
        public string challenge_Tittle;
    }

    
}
