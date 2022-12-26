using IDARENOW_WebServer.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using VaderSharp;

namespace IDARENOW_WebServer.Controllers
{
    public class CommentsController : ApiController
    {


        private AlphaRelevant_iDareNowEntities db = new AlphaRelevant_iDareNowEntities();
        
        //getAllComments
        [HttpGet]
        public IHttpActionResult getAllComments(int post_id)
        {
            List<Comments> listofcomments = (from comment in db.Video_comments
                                             where comment.Video_ID == post_id
                                             join acc in db.User_Accounts
                                             on comment.User_ID equals acc.User_ID
                                             select new Comments
                                             {
                                                 video_id = comment.Video_ID,
                                                 profile_id = acc.User_ID,
                                                 profile_image = Constants.ip_port_conn + "/Images/icons/" + acc.User_Profile_Picture,
                                                 comment = comment.Comment_text,
                                                 full_name = acc.User_FullName,
                                                 datetime = comment.Comment_Timestamp.ToString()
                                             }).ToList();
            return Ok(listofcomments);
        }

        [HttpPost]
        public  void postComment([FromBody] Comments comment)
        {
            if (comment != null)
            {
                DateTime dt = DateTime.Now;
                Video_comments vc = new Video_comments();
                vc.Comment_text = comment.comment;
                SentimentIntensityAnalyzer analyzer = new SentimentIntensityAnalyzer();
                var results = analyzer.PolarityScores(comment.comment);
                vc.Comment_score = results.Compound;
                vc.Comment_Timestamp = dt;
                vc.User_ID = comment.profile_id;
                vc.Video_ID = comment.video_id;
                db.Video_comments.Add(vc);
                db.SaveChanges();
            }
        }

    }
    public class Comments
    {
        public int video_id;
        public int profile_id;
        public string full_name;
        public string comment;
        public string profile_image;
        public string datetime;
    }
}
