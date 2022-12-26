using IDARENOW_WebServer.Models;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Threading.Tasks;
using System.Web;
using System.Web.Http;


namespace IDARENOW_WebServer.Controllers
{
    public class ProfileController : ApiController
    {
        // GET: Profile
        private AlphaRelevant_iDareNowEntities db = new AlphaRelevant_iDareNowEntities();
        // api/Signup/postRegister
        [HttpPost]
        [AllowAnonymous]
        //[Route("api/Profile/postProfileImage")]

        public HttpResponseMessage postProfileImage()
        {

            Dictionary<string, object> dict = new Dictionary<string, object>();
            try
            {

                var httpRequest = HttpContext.Current.Request;
                int id = Convert.ToInt16(httpRequest.Params.Get("id"));

                foreach (string file in httpRequest.Files)
                {
                    HttpResponseMessage response = Request.CreateResponse(HttpStatusCode.Created);

                    var postedFile = httpRequest.Files[file];
                    if (postedFile != null && postedFile.ContentLength > 0)
                    {

                        int MaxContentLength = 1024 * 1024 * 3; //Size = 3 MB  

                        IList<string> AllowedFileExtensions = new List<string> { ".jpg", ".gif", ".png" };
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
                            string key = GetUniqueKey(60) + extension;


                            var filePath = HttpContext.Current.Server.MapPath("~/Images/") + key;
                            var filePath_icon = HttpContext.Current.Server.MapPath("~/Images/icons/") + key;
                            User_Accounts ua = db.User_Accounts.Where(x => x.User_ID == id).FirstOrDefault();
                            ua.User_Profile_Picture = key;
                            db.SaveChanges();
                            using (MemoryStream memory = new MemoryStream())
                            {
                                using (FileStream fs = new FileStream(filePath, FileMode.Create, FileAccess.ReadWrite))
                                {
                                    Bitmap bmp = new Bitmap(postedFile.InputStream);
                                    bmp.Save(memory, System.Drawing.Imaging.ImageFormat.Jpeg);
                                    byte[] bytes = memory.ToArray();
                                    fs.Write(bytes, 0, bytes.Length);
                                }
                            }
                            Bitmap icon = ResizeBitmap(new Bitmap(postedFile.InputStream), 100,100);
                            using (MemoryStream memory = new MemoryStream())
                            {
                                using (FileStream fs = new FileStream(filePath_icon, FileMode.Create, FileAccess.ReadWrite))
                                {
                                    icon.Save(memory, System.Drawing.Imaging.ImageFormat.Jpeg);
                                    byte[] bytes = memory.ToArray();
                                    fs.Write(bytes, 0, bytes.Length);
                                }
                            }
                            //Bitmap bmp = new Bitmap(postedFile.InputStream);
                            //icon.Save(filePath_icon, System.Drawing.Imaging.ImageFormat.Icon);
                        }
                    }

                    var message1 = string.Format("Image Updated Successfully.");
                    return Request.CreateResponse(HttpStatusCode.Created, message1); ;
                }
                var res = string.Format("Please Upload a image.");
                dict.Add("error", res);
                return Request.CreateResponse(HttpStatusCode.NotFound, dict);
            }
            catch (Exception ex)
            {
                var res = string.Format(ex.Message);
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
        [HttpGet]
        public HttpResponseMessage GetProfileImage(int Id)
        {
            
            HttpResponseMessage response = new HttpResponseMessage();
            User_Accounts ua = db.User_Accounts.Where(x => x.User_ID == Id).SingleOrDefault();
            if (ua.User_Profile_Picture != "" & ua.User_Profile_Picture != null)
            {
                try
                {
                    var Fs = new FileStream(System.Web.Hosting.HostingEnvironment.MapPath("~/Images\\" + ua.User_Profile_Picture), FileMode.Open, FileAccess.ReadWrite, FileShare.ReadWrite);

                    Image img = Image.FromStream(Fs);
                    MemoryStream ms = new MemoryStream();
                    img.Save(ms, System.Drawing.Imaging.ImageFormat.Jpeg);
                    response.Content = new ByteArrayContent(ms.ToArray());

                    response.Content.Headers.ContentType = new MediaTypeHeaderValue("image/png");
                    response.StatusCode = HttpStatusCode.OK;

                    return response;
                }
                catch(FileNotFoundException e)
                {
                    response.StatusCode = HttpStatusCode.NotFound;
                    return response;
                }
                
            }
            response.StatusCode = HttpStatusCode.NotFound;
            return response;
        }
        
        public HttpResponseMessage getImage(int id)
        {
            HttpResponseMessage response = new HttpResponseMessage();
            User_Accounts ua = db.User_Accounts.Where(x => x.User_ID == id).SingleOrDefault();
            if (ua.User_Profile_Picture != "" & ua.User_Profile_Picture != null)
            {

                var Fs = new FileStream(System.Web.Hosting.HostingEnvironment.MapPath("~/Images\\" + ua.User_Profile_Picture), FileMode.Open, FileAccess.ReadWrite, FileShare.ReadWrite);

                Image img = Image.FromStream(Fs);
                MemoryStream ms = new MemoryStream();
                img.Save(ms, System.Drawing.Imaging.ImageFormat.Jpeg);
                response.Content = new ByteArrayContent(ms.ToArray());
                response.Content.Headers.ContentType = new MediaTypeHeaderValue("image/jpeg");
                response.StatusCode = HttpStatusCode.OK;

                return response;
            }
            response.StatusCode = HttpStatusCode.NotFound;
            return response;
        }



        [HttpGet]
        public IHttpActionResult getUserVideos(int user_id, int offset)
        {
            NewsfeedPost[] challenges = (from v in BaseClass.db.Videos
                                         where v.Video_to_user_id == user_id
                                         join challenge in BaseClass.db.Assign_Challenge
                                         on v.Challenge_id equals challenge.Challenge_ID
                                         where v.Video_isapproved == 1 && challenge.Challenge_Approval_Status == 4
                                         select new NewsfeedPost
                                         {
                                             challenge_id = challenge.Challenge_ID,
                                             Challenge_From_Name = BaseClass.db.User_Accounts.Where(x => x.User_ID == challenge.Challenge_From_ID).Select(y => y.User_FullName).FirstOrDefault(),
                                             Challenge_To_User_ID = challenge.Challenge_To_User_ID,
                                             c_description = challenge.Challenge_Description,
                                             challenge_type_id = challenge.Challenge_Type_ID,
                                             c_title = challenge.Challenge_Tittle,
                                             video_dateTime = v.Video_Upload_Date,
                                             video_id = BaseClass.db.Videos.Where(x => x.Challenge_id == challenge.Challenge_ID).Select(y => y.Video_ID).FirstOrDefault(),
                                             challenge_from_id = challenge.Challenge_From_ID,
                                             post_approve_reject = BaseClass.db.Post_approve_reject.Where(x => x.v_id == v.Video_ID).Where(y => y.user_id == user_id).Select(item => item.status).FirstOrDefault()
                                         }).ToList().OrderByDescending(c => c.challenge_id).Skip(offset).Take(3).ToArray();

            for (int i = 0; i < challenges.Length; i++)
            {
                challenges[i].dateTime = Constants.parseDate(challenges[i].video_dateTime.Ticks);
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
                    item.Challenge_To_User_ID = BaseClass.db.Videos.Where(x => x.Challenge_id == item.challenge_id).Select(y => y.Video_to_user_id).FirstOrDefault();
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

        public static Bitmap ResizeBitmap(Bitmap originalBitmap, int requiredHeight, int requiredWidth)
        {
            int[] heightWidthRequiredDimensions;

            // Pass dimensions to worker method depending on image type required
            heightWidthRequiredDimensions = WorkDimensions(originalBitmap.Height, originalBitmap.Width, requiredHeight, requiredWidth);


            Bitmap resizedBitmap = new Bitmap(heightWidthRequiredDimensions[1],
                                               heightWidthRequiredDimensions[0]);

            const float resolution = 72;

            resizedBitmap.SetResolution(resolution, resolution);

            Graphics graphic = Graphics.FromImage((Image)resizedBitmap);

            graphic.InterpolationMode = System.Drawing.Drawing2D.InterpolationMode.HighQualityBicubic;
            graphic.DrawImage(originalBitmap, 0, 0, resizedBitmap.Width, resizedBitmap.Height);

            graphic.Dispose();
            originalBitmap.Dispose();
            //resizedBitmap.Dispose(); // Still in use


            return resizedBitmap;
        }


        private static int[] WorkDimensions(int originalHeight, int originalWidth, int requiredHeight, int requiredWidth)
        {
            int imgHeight = 0;
            int imgWidth = 0;

            imgWidth = requiredHeight;
            imgHeight = requiredWidth;


            int requiredHeightLocal = originalHeight;
            int requiredWidthLocal = originalWidth;

            double ratio = 0;

            // Check height first
            // If original height exceeds maximum, get new height and work ratio.
            if (originalHeight > imgHeight)
            {
                ratio = double.Parse(((double)imgHeight / (double)originalHeight).ToString());
                requiredHeightLocal = imgHeight;
                requiredWidthLocal = (int)((decimal)originalWidth * (decimal)ratio);
            }

            // Check width second. It will most likely have been sized down enough
            // in the previous if statement. If not, change both dimensions here by width.
            // If new width exceeds maximum, get new width and height ratio.
            if (requiredWidthLocal >= imgWidth)
            {
                ratio = double.Parse(((double)imgWidth / (double)originalWidth).ToString());
                requiredWidthLocal = imgWidth;
                requiredHeightLocal = (int)((double)originalHeight * (double)ratio);
            }

            int[] heightWidthDimensionArr = { requiredHeightLocal, requiredWidthLocal };

            return heightWidthDimensionArr;
        }
    }


}