using System;
using System.Linq;
using System.Web.Http;
using System.Net.Http;
using System.Web;
using System.Collections.Generic;
using System.Net;
using System.Threading.Tasks;
using System.Diagnostics;
using ParallelDots;
using System.IO;
using System.Reflection;
using System.Configuration;
using IDARENOW_WebServer;
using IDARENOW_WebServer.Models;


namespace IDARENOW_WebServer.Controllers
{
    public class VideoController : ApiController
    {
        // GET: Videos

        private AlphaRelevant_iDareNowEntities db = new AlphaRelevant_iDareNowEntities();
        [HttpGet]
        public string GetVideoById(int id)
        {

            Videos _video = db.Videos.Where(x => x.Video_ID == id).SingleOrDefault();
            
            if (_video.Video_name != "")
            {
                string videoLink = _video.Video_name;
                return videoLink;
            }
            return "";
        }
        public HttpResponseMessage GetVideoStream()
        {
            Stream stream = new FileStream(HttpContext.Current.Server.MapPath("~")+"//ChallengeVideo/untochedVideos/s7-edge-video", FileMode.Open);
            string mediaType = "video/mp4";
            if (Request.Headers.Range != null)
            {
                // Return part of the video
                HttpResponseMessage partialResponse = Request.CreateResponse(HttpStatusCode.PartialContent);
                partialResponse.Content = new ByteRangeStreamContent(stream, Request.Headers.Range, mediaType);
                return partialResponse;
            }
            else
            {
                // Return complete video
                HttpResponseMessage fullResponse = Request.CreateResponse(HttpStatusCode.OK);
                fullResponse.Content = new StreamContent(stream);
                
                fullResponse.Content.Headers.ContentType = new System.Net.Http.Headers.MediaTypeHeaderValue(mediaType);
                return fullResponse;
            }
        }

      
        [HttpPost]
        [AllowAnonymous]
        public  HttpResponseMessage postChallengeVideo(string description,string c_id, int user_to_id)
        {
           
            Dictionary<string, object> dict = new Dictionary<string, object>();
            try
            {
                if (!Request.Content.IsMimeMultipartContent())
                {
                    throw new HttpResponseException(HttpStatusCode.UnsupportedMediaType);
                }

                var httpRequest = HttpContext.Current.Request;
                //string des = httpRequest.Params.Get("description");

                foreach (string file in httpRequest.Files)
                {
                    HttpResponseMessage response = Request.CreateResponse(HttpStatusCode.Created);

                    var postedFile = httpRequest.Files[file];
                    if (postedFile != null && postedFile.ContentLength > 0)
                    {

                        int MaxContentLength = 1024 * 1024 * 100; //Size = 50 MB  

                        IList<string> AllowedFileExtensions = new List<string> { "mp4","3gp" };
                        var ext = postedFile.FileName.Split('.')[1];
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
                            string updatedFileName = BaseClass.GetUniqueKey(60) +"."+ extension;

                            string completefileName = updatedFileName;
                            string user_id = c_id;
                            string fileName = completefileName.Split('.')[0];
                            string extension_f = completefileName.Split('.')[1];
                            UniquefolderName = user_id + "_folder";
                            assignFields(UniquefolderName, fileName + "." + extension_f);
                            checkCreateDirectory();
                            var filePath = HttpContext.Current.Server.MapPath("~/ChallengeVideo/"+UniquefolderName+"/inputVideo/") + updatedFileName;
                             postedFile.SaveAs(filePath);
                            //initializeCMD();

                            //Save unprocessed Videos
                            Videos _video = new Videos();
                            _video.Video_name = Path.GetFileNameWithoutExtension(updatedFileName)+convertText+".mp4";
                            _video.Video_Status = 0;
                            _video.Video_isapproved = 1;
                            _video.Video_Upload_Date = DateTime.Now;
                            _video.Challenge_id = Convert.ToInt32(c_id);
                            _video.Video_to_user_id = user_to_id;
                            db.Videos.Add(_video);
                            db.SaveChanges();
                            initializeCMD();
                            executeCommands(fileName, extension, Convert.ToInt16(c_id), _video.Video_ID);

                        }
                    }

                    var message1 = string.Format("Videos Updated Successfully.");
                    return Request.CreateResponse(HttpStatusCode.Created, message1); ;
                }
                var res = string.Format("Please Upload a image.");
                dict.Add("error", res);
                return Request.CreateErrorResponse(HttpStatusCode.NoContent,new Exception());
            }
            catch (Exception ex)
            {
                var res = string.Format(ex.Message);
                dict.Add("error", res);
                return Request.CreateResponse(HttpStatusCode.NotFound, dict);
            }
        }
          void ConvertTOMP4(String _cmd)
        {
            //chala ab cmd khulna chaye ;d 
            cmd.Start();
            cmd.StandardInput.WriteLine(_cmd);
            cmd.StandardInput.Close();
            string outp = cmd.StandardOutput.ReadToEnd();
            
            cmd.WaitForExit();
        }
          void createFrames(String _cmd)
        {
            cmd.Start();
            cmd.StandardInput.WriteLine(_cmd);
            cmd.StandardInput.Close();
           string outp = cmd.StandardOutput.ReadToEnd();
            cmd.WaitForExit();
        }
      public  void executeCommands(string inputFileName, string extension, int c_id, int Video_id)
        {
            String inputPather = HttpContext.Current.Server.MapPath("~")  + basePath.Remove(0,1) + uniqueIdentifier + "\\" + inputFilePath + "\\" + inputFileName + "." + extension;
            String outputPather = HttpContext.Current.Server.MapPath("~") + basePath.Remove(0, 1) + uniqueIdentifier + "\\" + inputFilePath + "\\" + inputFileName + convertText + Vextension;
            System.Diagnostics.ProcessStartInfo my = new System.Diagnostics.ProcessStartInfo();
            my.FileName = Environment.ExpandEnvironmentVariables("%SystemRoot%") + @"\System32\cmd.exe";
            //my.Arguments = "\\k E: && cd " + HttpContext.Current.Server.MapPath("~") + " && ffmpeg ";
            my.Arguments = "cmd /k E: && cd " + HttpContext.Current.Server.MapPath("~\\bin\\")+" && "+ "ffmpeg -i " + inputPather + " -f mp4 -vcodec libx264 -movflags faststart -preset fast -profile:v main -acodec aac " + outputPather + " -hide_banner";
            Process process = System.Diagnostics.Process.Start(my);
            //ConvertTOMP4("ffmpeg -i " + inputPather + " -f mp4 -vcodec libx264 -preset fast -profile:v main -acodec aac " + outputPather + " -hide_banner");
            //string inputFile = "input_path_goes_here";
            //var encoder = new FFMpeg();
            //var probe = new FFProbe();
            //var video = VideoInfo.FromPath(inputPather);
            //FileInfo outputFile = new FileInfo(outputPather);

            // easily track conversion progress
            //encoder.OnProgress += (percentage) => Console.WriteLine("Progress {0}%", percentage);
            //MP4 conversion
            //encoder.Convert(
            //    video,
            //    outputFile,
            //    VideoType.Mp4,
            //    Speed.UltraFast,
            //    VideoSize.Original,
            //    AudioQuality.Hd,
            //    true
            //);
            //outputFile.Refresh();
            //var outputFileVideoInfo = VideoInfo.FromPath(outputFile.DirectoryName +"\\"+ outputFile.Name);
            //int duration = getDuration(" ffprobe -i " + "." + basePath + uniqueIdentifier + "\\" + inputFilePath + "\\" + inputFileName + convertText + Vextension + " -v quiet -print_format json -show_format -show_streams -hide_banner");
            //int duration = outputFileVideoInfo.Duration.Seconds;
            //int delayFrames = 10;
            //int frames = duration / delayFrames;
            //string framescmd = HttpContext.Current.Server.MapPath("~")+"\\bin\\" + "ffmpeg -i " + input + " -y -vf fps=1/" + delayFrames + " " + framesOutputPath;
            //createFrames(framescmd);
            //dynamic cmd = new Cmd();
            //string abc =cmd.dir();

            //new FFMpeg()
            //    .Snapshot(
            //        outputFileVideoInfo,
            //        new FileInfo(framesOutputPath),
            //        new Size(200, 400),
            //        TimeSpan.FromSeconds(1)
            //    );

            //paralleldots pd = new paralleldots("XD67m4MDeTyoX25lokrNDrjceaDGUqkKDw1eOsalX1Y");
            //string extradigits = "";
            //decimal[] values = new decimal[frames];
            //for (int i = 1; i <= frames; i++)
            //{

            //    if (i < 10)
            //    {
            //        extradigits = "00";
            //    }
            //    else if (i >= 10)
            //    {
            //        extradigits = "0";
            //    }
            //    String path_to_image = "." + basePath + uniqueIdentifier + "\\" + "tempImages\\img" + extradigits + i + ".jpg";
            //    String nsfw = pd.nsfw(path_to_image);
            //    dynamic obj = Newtonsoft.Json.JsonConvert.DeserializeObject(nsfw);

            //    if (obj.output == "safe to open at work")
            //        values[i - 1] = Convert.ToDecimal(obj.prob);
            //}
            //double average = 0.0;
            //foreach (double item in values)
            //{
            //    average += item;

            //}
            //average = average / frames;
            //average *= 100;
            //if (average > 50)
            //{

            //    //Video_ratings _ratings = new Video_ratings();
            //    //_ratings.challenge_id = Convert.ToInt16(c_id);
            //    //_ratings.video_id = Video_id;
            //    //_db.Video_ratings.Add(_ratings);
            //    //_db.SaveChanges();
            //}
        }
        //public  void executeCommands(string inputFileName, string extension, int c_id, int Video_id)
        //{
        //    String inputPather = "." + basePath + uniqueIdentifier + "\\" + inputFilePath + "\\" + inputFileName  + extension;
        //    String outputPather = "." + basePath + uniqueIdentifier + "\\" + inputFilePath + "\\" + inputFileName + convertText + Vextension;
        //    initializeCMD();
        //    if (extension.Contains("mp4") )
        //    {
        //        string ta = "ffmpeg -i " + inputPather + " -f mp4 -vcodec libx264 -preset fast -profile:v main -acodec aac " + outputPather + " -hide_banner";
        //        ConvertTOMP4("ffmpeg -i " + inputPather + " -f mp4 -vcodec libx264 -preset fast -profile:v main -acodec aac " + outputPather + " -hide_banner");
        //    }

        //    //int duration = getDuration(HttpContext.Current.Server.MapPath("~//")+"ffprobe -i " + "." + basePath + uniqueIdentifier + "\\" + inputFilePath + "\\" + inputFileName + convertText + Vextension + " -v quiet -print_format json -show_format -show_streams -hide_banner");
        //    //int delayFrames = 10;
        //    //int frames = duration / delayFrames;
        //    int frames = 1;
        //    //createFrames("ffmpeg -i " + outputPather + " -y -vf fps=1/" + delayFrames + " " + framesOutputPath);
        //    //paralleldots pd = new paralleldots("XD67m4MDeTyoX25lokrNDrjceaDGUqkKDw1eOsalX1Y");
        //    //string extradigits = "";
        //    //decimal[] values = new decimal[frames];
        //    //for (int i = 1; i <= frames; i++)
        //    //{

        //    //    if (i < 10)
        //    //    {
        //    //        extradigits = "00";
        //    //    }
        //    //    else if (i >= 10)
        //    //    {
        //    //        extradigits = "0";
        //    //    }
        //    //    String path_to_image = "." + basePath + uniqueIdentifier + "\\" + "tempImages\\img" + extradigits + i + ".jpg";
        //    //    String nsfw = pd.nsfw(path_to_image);
        //    //    dynamic obj = Newtonsoft.Json.JsonConvert.DeserializeObject(nsfw);

        //    //    if (obj.output == "safe to open at work")
        //    //        values[i - 1] = Convert.ToDecimal(obj.prob);
        //    //}
        //    double average = 0.0;
        //    //foreach (double item in values)
        //    //{
        //    //    average += item;

        //    //}
        //    average = average / frames;
        //    average *= 100;


        //        Videos _ratings = new Videos();
        //        _ratings.Challenge_id = Convert.ToInt16(c_id);
        //        _ratings.Video_ID = Video_id;
        //        if (average >= 50)
        //            _ratings.Video_isapproved = 1;
        //        else
        //            _ratings.Video_isapproved = 0;

        //        _ratings.Video_isapproved = 1;
        //        db.Videos.Add(_ratings);
        //        db.SaveChanges();
        //        //change status to challenge completed
        //        int id = Convert.ToInt16(c_id);
        //        Assign_Challenge record = db.Assign_Challenge.Where(x => x.Challenge_ID == id).Select(t => t).FirstOrDefault();
        //        if (record != null && record.Challenge_Approval_Status == Constants.accepted_challenges)
        //        {
        //            record.Challenge_Approval_Status = Constants.completed_challenge;
        //        }
        //}
        public   void initializeCMD()
        {
            if (cmd == null)
            {
                cmd = new Process();
                cmd.StartInfo.FileName = "C:\\Windows\\System32\\cmd.exe";
                cmd.StartInfo.RedirectStandardInput = true;
                cmd.StartInfo.RedirectStandardOutput = true;
                cmd.StartInfo.CreateNoWindow = false;//false to display output
                cmd.StartInfo.UseShellExecute = false;
                cmd.StartInfo.LoadUserProfile = true;
            }
            
        }
        public   void assignFields(string id, string _inputFileName)
        {
            inputFileName = _inputFileName;
            uniqueIdentifier += id;//important to decide to whom video belongs
            outputFileName = "img%03d" + Iextension;
            framesOutputPath = "." + basePath + uniqueIdentifier + "\\" + outputFilePath + "\\" + outputFileName;
        }

        public   void checkCreateDirectory()
        {
            
            Directory.CreateDirectory(HttpContext.Current.Server.MapPath("~/"+ basePath));
            Directory.CreateDirectory(HttpContext.Current.Server.MapPath("~/" + basePath+uniqueIdentifier));
            Directory.CreateDirectory(HttpContext.Current.Server.MapPath("~/" + basePath + uniqueIdentifier + "\\" + inputFilePath));
            Directory.CreateDirectory(HttpContext.Current.Server.MapPath("~/" + basePath + uniqueIdentifier + "\\" + outputFilePath));
            //Directory.CreateDirectory(basePath+uniqueIdentifier);
            //Directory.CreateDirectory(basePath + uniqueIdentifier+"\\"+ inputFilePath);
            //Directory.CreateDirectory(basePath + uniqueIdentifier + "\\" + outputFilePath);
        }
          int getDuration(string commands)
        {
            cmd.Start();
            cmd.StandardInput.WriteLine(commands);
            cmd.StandardInput.Close();
            cmd.WaitForExit();
            String temp = cmd.StandardOutput.ReadToEnd();
            int start = temp.IndexOf('{');
            int end = temp.LastIndexOf('}');
            end++;
            dynamic obj = Newtonsoft.Json.JsonConvert.DeserializeObject(temp.Substring(start, end - start));
            cmd.WaitForExit();
            return (int)(double)obj.streams[0].duration;
        }
        public static Process cmd ;
          string Vextension = ".mp4";
          string Iextension = ".jpg";
          string convertText = "_converted";
          string inputFileName = "";
          string inputCropped = "";
          string outputFileName ;
          string _root = Environment.CurrentDirectory;
          string basePath = "\\ChallengeVideo";
          string uniqueIdentifier = "\\";
          string inputFilePath = "inputVideo";
          string outputFilePath = "tempImages";
          public   string UniquefolderName = "";
          string framesOutputPath = "";

       
    }
    class Program
    {
        //private static AlphaRelevant_iDareNowEntities _db = new AlphaRelevant_iDareNowEntities();

        //static void Main(string[] args)
        //{
        //    string completefileName = "tester.3gp";
        //    string user_id = "id1";
        //    string fileName = completefileName.Split('.')[0];
        //    string extension = completefileName.Split('.')[1];
        //    UniquefolderName = user_id + "_folder";
        //    assignFields(UniquefolderName, fileName + "." + extension);
        //    checkCreateDirectory();
        //    //upload video here                
        //    initializeCMD();


        //}

        //public static void executeCommands(string inputFileName, string extension, int c_id, int Video_id)
        //{
        //    String inputPather = "." + basePath + uniqueIdentifier + "\\" + inputFilePath + "\\" + inputFileName + "." + extension;
        //    String outputPather = "." + basePath + uniqueIdentifier + "\\" + inputFilePath + "\\" + inputFileName + convertText + Vextension;
        //    ConvertTOMP4("ffmpeg -i " + inputPather + " -f mp4 -vcodec libx264 -preset fast -profile:v main -acodec aac " + outputPather + " -hide_banner");
        //    int duration = getDuration(" ffprobe -i " + "." + basePath + uniqueIdentifier + "\\" + inputFilePath + "\\" + inputFileName + convertText + Vextension + " -v quiet -print_format json -show_format -show_streams -hide_banner");
        //    int delayFrames = 10;
        //    int frames = duration / delayFrames;

        //    createFrames("ffmpeg -i " + outputPather + " -y -vf fps=1/" + delayFrames + " " + framesOutputPath);
        //    paralleldots pd = new paralleldots("XD67m4MDeTyoX25lokrNDrjceaDGUqkKDw1eOsalX1Y");
        //    string extradigits = "";
        //    decimal[] values = new decimal[frames];
        //    for (int i = 1; i <= frames; i++)
        //    {

        //        if (i < 10)
        //        {
        //            extradigits = "00";
        //        }
        //        else if (i >= 10)
        //        {
        //            extradigits = "0";
        //        }
        //        String path_to_image = "." + basePath + uniqueIdentifier + "\\" + "tempImages\\img" + extradigits + i + ".jpg";
        //        String nsfw = pd.nsfw(path_to_image);
        //        dynamic obj = Newtonsoft.Json.JsonConvert.DeserializeObject(nsfw);

        //        if (obj.output == "safe to open at work")
        //            values[i - 1] = Convert.ToDecimal(obj.prob);
        //    }
        //    double average = 0.0;
        //    foreach (double item in values)
        //    {
        //        average += item;

        //    }
        //    average = average / frames;
        //    average *= 100;
        //    if (average > 50)
        //    {

        //        Video_ratings _ratings = new Video_ratings();
        //        _ratings.challenge_id = Convert.ToInt16(c_id);
        //        _ratings.video_id = Video_id;
        //        _db.Video_ratings.Add(_ratings);
        //        _db.SaveChanges();
        //    }
        //}
        //public static void initializeCMD()
        //{
        //    cmd = new Process();
        //    cmd.StartInfo.FileName = "cmd.exe";
        //    cmd.StartInfo.RedirectStandardInput = true;
        //    cmd.StartInfo.RedirectStandardOutput = true;
        //    cmd.StartInfo.CreateNoWindow = false;//false to display output
        //    cmd.StartInfo.UseShellExecute = false;
        //}
        //public static void assignFields(string id, string _inputFileName)
        //{
        //    inputFileName = _inputFileName;
        //    uniqueIdentifier += id;//important to decide to whom video belongs
        //    framesOutputPath = "." + basePath + uniqueIdentifier + "\\" + outputFilePath + "\\" + outputFileName;
        //}

        //public static void checkCreateDirectory()
        //{
        //    Directory.CreateDirectory(Path.Combine(Environment.CurrentDirectory, basePath));
        //    Directory.CreateDirectory(Path.Combine(Environment.CurrentDirectory + basePath, uniqueIdentifier));
        //    Directory.CreateDirectory(Path.Combine(Environment.CurrentDirectory + basePath + uniqueIdentifier, inputFilePath));
        //    Directory.CreateDirectory(Path.Combine(Environment.CurrentDirectory + basePath + uniqueIdentifier, outputFilePath));
        //}
        //static int getDuration(string commands)
        //{

        //    cmd.Start();
        //    cmd.StandardInput.WriteLine(commands);
        //    cmd.StandardInput.Close();
        //    String temp = cmd.StandardOutput.ReadToEnd();
        //    int start = temp.IndexOf('{');
        //    int end = temp.LastIndexOf('}');
        //    end++;
        //    dynamic obj = Newtonsoft.Json.JsonConvert.DeserializeObject(temp.Substring(start, end - start));
        //    cmd.WaitForExit();
        //    return (int)(double)obj.streams[0].duration;
        //}

    }
}