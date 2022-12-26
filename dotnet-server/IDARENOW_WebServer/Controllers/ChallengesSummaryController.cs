using IDARENOW_WebServer.Models;
using System;
using System.Collections.Generic;
using System.Data.Entity.Core.Objects;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;

namespace IDARENOW_WebServer.Controllers
{
    public class ChallengesSummaryController : ApiController
    {

        private AlphaRelevant_iDareNowEntities db = new AlphaRelevant_iDareNowEntities();

        [HttpGet]
        public IHttpActionResult getChallengesList(int user_id,int approval_status, int offset)
        {
            List<ChallengeSummary> challenges = (from challenge in db.Assign_Challenge
                                                 where challenge.Challenge_To_User_ID == user_id && approval_status == challenge.Challenge_Approval_Status
                                                 join acc in db.User_Accounts
                                                 on challenge.Challenge_From_ID equals acc.User_ID
                                                 select new ChallengeSummary
                                                 {
                                                     challenge_ID = challenge.Challenge_ID,
                                                     challenge_From_ID = acc.User_ID,
                                                     challenge_From_Profile_img = Constants.ip_port_conn + "/Images/icons/" + acc.User_Profile_Picture,
                                                     challenge_From_Name = acc.User_FullName,
                                                     challenge_Created_Date = challenge.Challenge_Created_Date,
                                                     challenge_Tittle = challenge.Challenge_Tittle,
                                                     challenge_Approval_Status = challenge.Challenge_Approval_Status,
                                                     challenge_Type_ID = challenge.Challenge_Type_ID
                                                 }).OrderByDescending(c => c.challenge_ID).Skip(offset).Take(10).ToList();
            for (int i = 0; i < challenges.Count; i++)
            {
                challenges[i].timeAgo = Constants.parseDate(challenges[i].challenge_Created_Date.Ticks);
            }
            return Ok(challenges);
        }


        [HttpGet]
        public IHttpActionResult getRespondChallengesList(int user_id, int approval_status, int offset)
        {
            List<ChallengeSummary> challenges = (from challenge in db.Assign_Challenge
                                                 where challenge.Challenge_To_User_ID == user_id && approval_status == challenge.Challenge_Approval_Status && System.Data.Entity.DbFunctions.AddDays(challenge.Challenge_Created_Date, (Int32?)challenge.Challenge_Duration) > DateTime.Now
                                                 join acc in db.User_Accounts
                                                 on challenge.Challenge_From_ID equals acc.User_ID
                                                 select new ChallengeSummary
                                                 {
                                                     challenge_ID = challenge.Challenge_ID,
                                                     challenge_From_ID = acc.User_ID,
                                                     challenge_From_Profile_img = Constants.ip_port_conn + "/Images/icons/" + acc.User_Profile_Picture,
                                                     challenge_From_Name = acc.User_FullName,
                                                     challenge_Created_Date = challenge.Challenge_Created_Date,
                                                     challenge_Tittle = challenge.Challenge_Tittle,
                                                     challenge_Approval_Status = challenge.Challenge_Approval_Status,
                                                     challenge_Type_ID = challenge.Challenge_Type_ID
                                                 }).OrderByDescending(c => c.challenge_ID).Skip(offset).Take(10).ToList();
            for (int i = 0; i < challenges.Count; i++)
            {
                challenges[i].timeAgo = Constants.parseDate(challenges[i].challenge_Created_Date.Ticks);
            }
            return Ok(challenges);
        }

        [HttpGet]
        public IHttpActionResult getUncompletedChallengesList(int user_id, int approval_status, int offset)
        {
            List<ChallengeSummary> challenges = (from vid in db.Videos
                                                 join challenge in db.Assign_Challenge
                                                 on vid.Challenge_id equals challenge.Challenge_ID
                                                 where challenge.Challenge_To_User_ID == user_id && vid.Challenge_id != challenge.Challenge_ID && (Constants.initial_challenge == challenge.Challenge_Approval_Status || Constants.un_completed_challenge == challenge.Challenge_Approval_Status || Constants.accepted_challenges == challenge.Challenge_Approval_Status) && (System.Data.Entity.DbFunctions.AddDays(challenge.Challenge_Created_Date, (Int32?)challenge.Challenge_Duration) < DateTime.Now)
                                                 join acc in db.User_Accounts
                                                 on challenge.Challenge_From_ID equals acc.User_ID

                                                 select new ChallengeSummary
                                                 {
                                                     challenge_ID = challenge.Challenge_ID,
                                                     challenge_From_ID = acc.User_ID,
                                                     challenge_From_Profile_img = Constants.ip_port_conn + "/Images/icons/" + acc.User_Profile_Picture,
                                                     challenge_From_Name = acc.User_FullName,
                                                     challenge_Created_Date = challenge.Challenge_Created_Date,
                                                     challenge_Tittle = challenge.Challenge_Tittle,
                                                     challenge_Approval_Status = challenge.Challenge_Approval_Status,
                                                     challenge_Type_ID = challenge.Challenge_Type_ID
                                                 }).OrderByDescending(c => c.challenge_ID).Skip(offset).Take(10).ToList();
            for (int i = 0; i < challenges.Count; i++)
            {
                challenges[i].timeAgo = Constants.parseDate(challenges[i].challenge_Created_Date.Ticks);
            }
            return Ok(challenges);
        }

        [HttpGet]
        public IHttpActionResult getInProgressList(int user_id, int approval_status, int offset)
        {
            List<ChallengeSummary> challenges = (from challenge in db.Assign_Challenge
                                                 where System.Data.Entity.DbFunctions.AddDays(challenge.Challenge_Created_Date, (Int32?)challenge.Challenge_Duration) > DateTime.Now
                                                 join acc in db.User_Accounts
                                                 on challenge.Challenge_From_ID equals acc.User_ID
                                                 join v in db.Videos
                                                 on challenge.Challenge_ID equals v.Challenge_id
                                                 where approval_status == challenge.Challenge_Approval_Status || user_id == v.Video_to_user_id
                                                 select new ChallengeSummary
                                                 {
                                                     challenge_ID = challenge.Challenge_ID,
                                                     challenge_From_ID = acc.User_ID,
                                                     challenge_From_Profile_img = Constants.ip_port_conn + "/Images/icons/" + acc.User_Profile_Picture,
                                                     challenge_From_Name = acc.User_FullName,
                                                     challenge_Created_Date = challenge.Challenge_Created_Date,
                                                     challenge_Tittle = challenge.Challenge_Tittle,
                                                     challenge_Approval_Status = challenge.Challenge_Approval_Status,
                                                     challenge_Type_ID = challenge.Challenge_Type_ID
                                                 }).OrderByDescending(c => c.challenge_ID).Skip(offset).Take(10).ToList();
            for (int i = 0; i < challenges.Count; i++)
            {
                challenges[i].timeAgo = Constants.parseDate(challenges[i].challenge_Created_Date.Ticks);
            }
            return Ok(challenges);
        }



        [HttpGet]
        public IHttpActionResult getChallengesByYou(int user_id , int offset)
        {
            List<ChallengeSummary> challenges = (from challenge in db.Assign_Challenge
                                                 where challenge.Challenge_From_ID == user_id
                                                 //join acc in db.User_Accounts
                                                 //on challenge.Challenge_From_ID equals acc.User_ID
                                                 select new ChallengeSummary
                                                 {
                                                     challenge_ID = challenge.Challenge_ID,
                                                     challenge_From_ID = challenge.Challenge_From_ID,
                                                     challenge_To_ID = challenge.Challenge_To_User_ID,
                                                     challenge_From_Profile_img = Constants.ip_port_conn + "/Images/icons/" + db.User_Accounts.Where(x => x.User_ID == challenge.Challenge_To_User_ID).Select(y => y.User_Profile_Picture).FirstOrDefault(),
                                                     challenge_From_Name = db.User_Accounts.Where(x => x.User_ID == challenge.Challenge_To_User_ID).Select(y => y.User_FullName).FirstOrDefault(),
                                                     challenge_Created_Date = challenge.Challenge_Created_Date,
                                                     challenge_Tittle = challenge.Challenge_Tittle,
                                                     challenge_Approval_Status = challenge.Challenge_Approval_Status,
                                                     challenge_Type_ID = challenge.Challenge_Type_ID
                                                 }).OrderByDescending(c => c.challenge_ID).Skip(offset).Take(10).ToList();
            for (int i = 0; i < challenges.Count; i++)
            {
                challenges[i].timeAgo = Constants.parseDate(challenges[i].challenge_Created_Date.Ticks);
            }
            return Ok(challenges);
        }


        public class ChallengeSummary
        {
            public int challenge_ID { get; set; }
            public int challenge_From_ID { get; set; }
            public String challenge_From_Name { get; set; }
            public String challenge_From_Profile_img { get; set; }
            public int? challenge_To_ID { get; set; }
            public DateTime challenge_Created_Date { get; set; }
            public String timeAgo { get; set; }
            public String challenge_Tittle { get; set; }
            public int challenge_Type_ID { get;  set;}
            public Nullable<int> challenge_Approval_Status { get; set; }
        }
    }
}
