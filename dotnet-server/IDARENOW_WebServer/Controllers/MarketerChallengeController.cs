using IDARENOW_WebServer.Models;
using System;
using System.Collections.Generic;
using System.Data.Entity.Core.Objects;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using static IDARENOW_WebServer.Controllers.ChallengesSummaryController;

namespace IDARENOW_WebServer.Controllers
{
    public class MarketerChallengeController : ApiController
    {
        
        private AlphaRelevant_iDareNowEntities db = new AlphaRelevant_iDareNowEntities();
        [HttpGet]
        public IHttpActionResult getMarketerChallengesList(int offset)
        {
            List<ChallengeSummary> challenges = (from challenge in db.Assign_Challenge
                                                 where challenge.Challenge_Type_ID == 2 && System.Data.Entity.DbFunctions.AddDays(challenge.Challenge_Created_Date, challenge.Challenge_Duration) > DateTime.Now
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


        [HttpPost]
        public IHttpActionResult postMarketerChallenge([FromBody] MarketerChallengeModel marketerChallenge)
        {
           
                if(marketerChallenge != null)
                {
                    Assign_Challenge ac = new Assign_Challenge();
                    ac.Challenge_Tittle = marketerChallenge.challenge_Tittle;
                    ac.Challenge_Description = marketerChallenge.challenge_Description;
                    ac.Challenge_Duration = marketerChallenge.challenge_Duration;
                    ac.Challenge_Amount = marketerChallenge.challenge_Amount;
                    ac.Challenge_From_ID = marketerChallenge.challenge_From_ID;
                    ac.Challenge_Created_Date = System.DateTime.Now;
                    if (marketerChallenge.challenge_To_ID == 0)
                    {
                        ac.Challenge_Type_ID = 2;
                    }
                    else if (marketerChallenge.challenge_To_ID > 0)
                    {
                        ac.Challenge_Type_ID = 1;
                        ac.Challenge_Approval_Status = Constants.initial_challenge;
                        ac.Challenge_To_User_ID = marketerChallenge.challenge_To_ID;
                    }
                    ac.Challenge_Status = 0;
                    db.Assign_Challenge.Add(ac);
                    db.SaveChanges();
                    BaseClass.assignChallengeNotification(ac.Challenge_ID);
                    return Ok(true);
                }
                return Ok(false);
        }

        [HttpGet]
        public IHttpActionResult getCheckUserCompletedVideo(int user_id, int challenge_id)
        {
            Videos video = db.Videos.Where(x => x.Challenge_id == challenge_id && user_id == x.Video_to_user_id).FirstOrDefault();
            if(video == null)
            {
                return Ok(true);
            }
            else if (user_id == video.Video_to_user_id)
            {
                return Ok(false);
            }
            return Ok(true);
        }



        [HttpPost]
        public IHttpActionResult postMarketerSelectWinner(int user_to_id, int challenge_id)
        {

            if (user_to_id > 0 && challenge_id > 0)
            {
                Assign_Challenge ac = db.Assign_Challenge.SingleOrDefault(x=>x.Challenge_ID == challenge_id);
                ac.Challenge_Winner_ID = user_to_id;
                ac.Challenge_Approval_Status = Constants.completed_challenge;
                if (ac.Challenge_Amount > 0)
                {
                    Transaction trans = new Transaction();
                    trans.Challenge_ID = ac.Challenge_ID;
                    trans.Credit_User_ID = ac.Challenge_From_ID;
                    trans.Debit_User_ID = user_to_id;
                    db.Transactions.Add(trans);
                    User_Accounts ua1 = db.User_Accounts.SingleOrDefault(x => x.User_ID == ac.Challenge_From_ID);
                    User_Accounts ua2 = db.User_Accounts.SingleOrDefault(x => x.User_ID == user_to_id);
                    ua1.User_Balance_Total_Amount -= (Double)ac.Challenge_Amount;
                    ua2.User_Balance_Total_Amount += (Double)ac.Challenge_Amount;
                }
                db.SaveChanges();
                return Ok(true);
            }
            return Ok(false);
        }

        [HttpGet]
        public int? getWinnerID_ifExists(int challenge_id)
        {
            int? winner_id = 0;
            winner_id = db.Assign_Challenge.Where(x => x.Challenge_ID == challenge_id).Select(y => y.Challenge_Winner_ID).Single();
            if (winner_id == null)
                return winner_id = 0;
            return winner_id;
        }
    }



  


    public class MarketerChallengeModel
    {
        public int challenge_From_ID { get; set; }
        public int challenge_To_ID { get; set; }
        public string challenge_Description { get; set; }
        public int challenge_Duration { get; set; }
        public string challenge_Tittle { get; set; }
        public Nullable<double> challenge_Amount { get; set; }
    }
}
