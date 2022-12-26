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

        public async Task<HttpResponseMessage> postProfileImage()
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
                            User_Accounts ua = db.User_Accounts.Where(x => x.User_ID == id).FirstOrDefault();
                            ua.User_Profile_Picture = key;
                            db.SaveChanges();
                            postedFile.SaveAs(filePath);

                        }
                    }

                    var message1 = string.Format("Image Updated Successfully.");
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
        
        public HttpResponseMessage GetProfileImage(int Id)
        {
            
            HttpResponseMessage response = new HttpResponseMessage();
            User_Accounts ua = db.User_Accounts.Where(x => x.User_ID == Id).SingleOrDefault();
            if (ua.User_Profile_Picture != "" & ua.User_Profile_Picture != null)
            {

                var Fs = new FileStream(System.Web.Hosting.HostingEnvironment.MapPath("~/Images\\" + ua.User_Profile_Picture),FileMode.Open,FileAccess.ReadWrite,FileShare.ReadWrite);
                
                Image img = Image.FromStream(Fs);
                MemoryStream ms = new MemoryStream();
                img.Save(ms, System.Drawing.Imaging.ImageFormat.Jpeg);
                response.Content = new ByteArrayContent(ms.ToArray());

                response.Content.Headers.ContentType = new MediaTypeHeaderValue("image/png");
                response.StatusCode = HttpStatusCode.OK;

                return response;
            }
            response.StatusCode = HttpStatusCode.NotFound ;
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
    }
}