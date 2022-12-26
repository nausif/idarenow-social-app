
using IDARENOW_WebServer.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;

namespace IDARENOW_WebServer.Controllers
{
    public class TranscationsController : ApiController
    {
        private AlphaRelevant_iDareNowEntities db = new AlphaRelevant_iDareNowEntities();

        [HttpGet]
        public double getUsertotalBalanceAmount(int user_id)
        {
            var ua = db.User_Accounts.Where(x => x.User_ID == user_id).FirstOrDefault();
            double d = ua.User_Balance_Total_Amount;
            return d;
        }



        [HttpGet]
        public IHttpActionResult getTranscationsList(int user_id, int offset)
        {
            List<TranscationsDetails> challenges = (from challenge in db.Assign_Challenge
                                                    where (challenge.Challenge_From_ID == user_id || challenge.Challenge_Winner_ID == user_id) && challenge.Challenge_Approval_Status == 4 && challenge.Challenge_Amount != null && challenge.Challenge_Amount != 0
                                                    join acc in db.User_Accounts
                                                    on challenge.Challenge_From_ID equals acc.User_ID
                                                    join trans in db.Transactions
                                                    on challenge.Challenge_ID equals trans.Challenge_ID
                                                    select new TranscationsDetails
                                                    {
                                                        challenge_ID = challenge.Challenge_ID,
                                                        challenge_From_ID = acc.User_ID,
                                                        challenge_From_Profile_img = Constants.ip_port_conn + "/Images/icons/" + acc.User_Profile_Picture,
                                                        challenge_From_Name = acc.User_FullName,
                                                        challenge_Created_Date = challenge.Challenge_Created_Date,
                                                        challenge_Tittle = challenge.Challenge_Tittle,
                                                        challenge_Approval_Status = challenge.Challenge_Approval_Status,
                                                        challenge_Type_ID = challenge.Challenge_Type_ID,
                                                        credit_ID = trans.Credit_User_ID,
                                                        challenge_amount = challenge.Challenge_Amount,
                                                        debit_ID = trans.Debit_User_ID
                                                 }).OrderByDescending(c => c.challenge_ID).Skip(offset).Take(10).ToList();
            for (int i = 0; i < challenges.Count; i++)
            {
                challenges[i].timeAgo = Constants.parseDate(challenges[i].challenge_Created_Date.Ticks);
            }
            return Ok(challenges);
        }
    }
    public class TranscationsDetails
    {
        public int challenge_ID { get; set; }
        public int challenge_From_ID { get; set; }
        public String challenge_From_Name { get; set; }
        public String challenge_From_Profile_img { get; set; }
        public Nullable<double> challenge_amount { get; set; }
        public int credit_ID { get; set; }
        public int debit_ID { get; set; }
        public DateTime challenge_Created_Date { get; set; }
        public String timeAgo { get; set; }
        public String challenge_Tittle { get; set; }
        public int challenge_Type_ID { get; set; }
        public Nullable<int> challenge_Approval_Status { get; set; }
    }
}
