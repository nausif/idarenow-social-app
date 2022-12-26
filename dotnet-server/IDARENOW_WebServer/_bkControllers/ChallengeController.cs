using System;
using IDARENOW_WebServer.Models;
using System.Linq;
using System.Web.Http;
using System.Net.Http;
using System.Web;
using System.Collections.Generic;
using System.Net;
using System.Threading.Tasks;

namespace IDARENOW_WebServer.Controllers
{
    public class ChallengeController : ApiController
    {
        private AlphaRelevant_iDareNowEntities db = new AlphaRelevant_iDareNowEntities();

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
                from.Challenge_notification_id = BaseClass.addNotification(type.challengeTo, 0);
                db.Assign_Challenge.Add(from);
                db.SaveChanges();
                return Ok(true);
            }
            catch(Exception e)
            {
                return Ok(false);
            }

        }


        [HttpPost]
        [AllowAnonymous]
        //[Route("api/Profile/postProfileImage")]

        public async Task<HttpResponseMessage> postChallengeVideo()
        {

            Dictionary<string, object> dict = new Dictionary<string, object>();
            try
            {

                var httpRequest = HttpContext.Current.Request;
                int id = Convert.ToInt16(httpRequest.Params.Get("id"));
                //int des = Convert.ToInt16(httpRequest.Params.Get("description"));

                foreach (string file in httpRequest.Files)
                {
                    HttpResponseMessage response = Request.CreateResponse(HttpStatusCode.Created);

                    var postedFile = httpRequest.Files[file];
                    if (postedFile != null && postedFile.ContentLength > 0)
                    {

                        int MaxContentLength = 1024 * 1024 * 50; //Size = 50 MB  

                        IList<string> AllowedFileExtensions = new List<string> { ".mp4", ".3gp" };
                        var ext = postedFile.FileName.Substring(postedFile.FileName.LastIndexOf('.'));
                        var extension = ext.ToLower();
                        if (!AllowedFileExtensions.Contains(extension))
                        {

                            var message = string.Format("Please Upload image of type .jpg,.gif,.png.");

                            dict.Add("error", message);
                            return Request.CreateResponse(HttpStatusCode.BadRequest, dict);
                        }
                        else if (postedFile.ContentLength > MaxContentLength)
                        {

                            var message = string.Format("Please Upload a file upto 1 mb.");

                            dict.Add("error", message);
                            return Request.CreateResponse(HttpStatusCode.BadRequest, dict);
                        }
                        else
                        {
                            string updatedFileName = GetUniqueKey(60) + extension;


                            var filePath = HttpContext.Current.Server.MapPath("~/Video/") + updatedFileName;
                            postedFile.SaveAs(filePath);

                        }
                    }

                    var message1 = string.Format("Video Updated Successfully.");
                    return Request.CreateErrorResponse(HttpStatusCode.Created, message1); ;
                }
                var res = string.Format("Please Upload a image.");
                dict.Add("error", res);
                return Request.CreateResponse(HttpStatusCode.NotFound, dict);
            }
            catch (Exception ex)
            {
                var res = string.Format("some Message");
                dict.Add("error", res);
                return Request.CreateResponse(HttpStatusCode.NotFound, dict);
            }
        }
        public string GetUniqueKey(int length)
        {
            string guidResult = string.Empty;

            while (guidResult.Length < length)
            {
                // Get the GUID.
                guidResult += Guid.NewGuid().ToString().GetHashCode().ToString("x");
            }

            // Make sure length is valid.
            if (length <= 0 || length > guidResult.Length)
                throw new ArgumentException("Length must be between 1 and " + guidResult.Length);

            // Return the first length bytes.
            return guidResult.Substring(0, length);
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
}
