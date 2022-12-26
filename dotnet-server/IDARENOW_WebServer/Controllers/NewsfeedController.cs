using IDARENOW_WebServer.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Http;

namespace IDARENOW_WebServer.Controllers
{
    public class NewsfeedController : ApiController
    {

         private AlphaRelevant_iDareNowEntities db = new AlphaRelevant_iDareNowEntities();
        [HttpGet]
        // GET: Newsfeed
       public IHttpActionResult getNewsFeedList(int user_id,int offset)
        {
            NewsfeedPost[] challenges = (from v in db.Videos
                                         join challenge in db.Assign_Challenge
                                         on v.Challenge_id equals challenge.Challenge_ID
                                         where challenge.Challenge_Approval_Status != 4
                                         join ua in db.User_Accounts
                                         on v.Video_to_user_id equals ua.User_ID
                                         select new NewsfeedPost
                                         {
                                             challenge_id = challenge.Challenge_ID,
                                             Challenge_To_User_ID = challenge.Challenge_To_User_ID,
                                             Challenge_To_Name = ua.User_FullName,
                                             c_description = challenge.Challenge_Description,
                                             challenge_type_id = challenge.Challenge_Type_ID,
                                             c_title = challenge.Challenge_Tittle,
                                             video_dateTime = v.Video_Upload_Date,
                                             video_id = v.Video_ID,
                                             challenge_from_id = challenge.Challenge_From_ID
                                             
                                         }).ToList().OrderByDescending(c => c.challenge_id).Skip(offset).Take(3).ToArray();
            
            for (int i = 0; i < challenges.Length; i++)
            {
               
                challenges[i].dateTime = Constants.parseDate(challenges[i].video_dateTime.Ticks);
                int t = challenges[i].video_id;
                challenges[i].post_approve_reject = BaseClass.db.Post_approve_reject.Where(x => x.v_id == t).Where(y => y.user_id == user_id).Select(item => item.status).FirstOrDefault();
                challenges[i].approve_ratio = db.Post_approve_reject.Where(x => x.v_id == t).Where(y => y.status == 1).Count();
                challenges[i].reject_ratio = db.Post_approve_reject.Where(x => x.v_id == t).Where(y => y.status == 2).Count();
            }
            foreach (var item in challenges)
            {
                
                    item.Challenge_From_Name = BaseClass.db.User_Accounts.Where(x => x.User_ID == item.challenge_from_id).Select(y => y.User_FullName).FirstOrDefault();
                    item.challenge_to_img = Constants.ip_port_conn + "/Images/icons/" + BaseClass.db.User_Accounts.Where(x => x.User_ID == item.Challenge_To_User_ID).Select(y => y.User_Profile_Picture).FirstOrDefault();
                    item.video = Constants.ip_port_conn + "/ChallengeVideo/"+item.challenge_id+"_folder/inputVideo/" + BaseClass.db.Videos.Where(x=>x.Video_ID == item.video_id).Select(y=>y.Video_name).FirstOrDefault();
                //if (item.video_id > 0)
                //{
                //    Video_comments[] comm = BaseClass.db.Video_comments.Where(x => x.Video_ID == item.video_id).ToArray();
                //    item.comments = (from comment in comm select new VideoComment {user_id = comment.User_ID, _comment =comment.Comment_text ,name = BaseClass.db.User_Accounts.Where(x=>x.User_ID == comment.User_ID).Select(x=>x.User_FullName).FirstOrDefault() }).ToArray();
                //}
            }
            return Ok(challenges);
        }
        [HttpPost]
        public IHttpActionResult ApproveRejectPost(int c_id,int user_id,int status)
        {
            
            int _status = BaseClass.db.Post_approve_reject.Where(x => x.v_id == c_id).Where(y => y.user_id == user_id).Select(item => item.status).FirstOrDefault();
            if (_status > 0)
            {
                //update
                Post_approve_reject obj = BaseClass.db.Post_approve_reject.Where(x => x.v_id == c_id).Where(y => y.user_id == user_id).SingleOrDefault();
                if (obj != null)
                {
                    obj.status = status;
                    BaseClass.db.SaveChanges();
                    AcceptRejectPost arp = new AcceptRejectPost();
                    arp.id = obj.id;
                    arp.user_id = obj.user_id;
                    arp.v_id = obj.v_id;
                    arp.status = obj.status;
                    int t = obj.v_id;
                    arp.approve_ratio = db.Post_approve_reject.Where(x => x.v_id == t).Where(y => y.status == 1).Count();
                    arp.reject_ratio = db.Post_approve_reject.Where(x => x.v_id == t).Where(y => y.status == 2).Count();
                    return Ok(arp);
                }
                return NotFound();
            }else
            {
                //Insert
                Post_approve_reject obj = new Post_approve_reject();
                obj.v_id = c_id;
                obj.user_id = user_id;
                obj.status = status;
                BaseClass.db.Post_approve_reject.Add(obj);
                BaseClass.db.SaveChanges();

                Post_approve_reject obj2 = BaseClass.db.Post_approve_reject.Where(x => x.v_id == c_id).Where(y => y.user_id == user_id).SingleOrDefault();

                AcceptRejectPost arp = new AcceptRejectPost();
                arp.id = obj2.id;
                arp.user_id = obj2.user_id;
                arp.v_id = obj2.v_id;
                arp.status = obj2.status;
                int t = obj2.v_id;
                arp.approve_ratio = db.Post_approve_reject.Where(x => x.v_id == t).Where(y => y.status == 1).Count();
                arp.reject_ratio = db.Post_approve_reject.Where(x => x.v_id == t).Where(y => y.status == 2).Count();
                return Ok(arp);
            }
        }

    }

    public class AcceptRejectPost
    {
        public int id { get; set; }
        public int v_id { get; set; }
        public int user_id { get; set; }
        public int status { get; set; }
        public int approve_ratio { get; set; }
        public int reject_ratio { get; set; }
    }

    public class NewsfeedPost
    {
        public int  challenge_id;
        public int challenge_type_id;
        public string c_title;
        public string c_description;
        public int challenge_from_id;
        public int? Challenge_To_User_ID;
        public string Challenge_From_Name;
        public string Challenge_To_Name;
        public string challenge_to_img;
        public DateTime video_dateTime;
        public DateTime challenge_dateTime;
        public string dateTime;
        public int video_id;
        public string video;
        public int post_approve_reject;
        public VideoComment[] comments;
        public int challenge_Expiry_Date;
        public int challenge_Duration;
        public int? winner_ID;
        public int approve_ratio;
        public int reject_ratio;
    }
    public class VideoComment
    {
        public int user_id;
        public string name;
        public string _comment;
    }
}