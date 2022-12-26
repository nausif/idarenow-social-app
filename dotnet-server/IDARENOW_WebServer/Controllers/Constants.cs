
using System;
using System.Web;

namespace IDARENOW_WebServer.Controllers
{
    public class Constants
    {
        public static string images_path = HttpContext.Current.Server.MapPath("~/Images/");
        public static string icons_path = HttpContext.Current.Server.MapPath("~/Images/icons/");
        public static string video_path = HttpContext.Current.Server.MapPath("~/Video/");
        //public static string ip_port_conn = "192.168.0.110:50017";
        public static string ip_port_conn = "192.168.1.103:50017";
        public static string ChallengeString = "CHALLENGE";

        public static int GMT_positive = 5;
        public static string TimestamptoDTComplete = "MM/dd/yyyy HH:mm:ss";
        public static string TimestamptoDTPartial = "MM/dd HH:mm";

        public static int initial_challenge = 1;
        public static int accepted_challenges = 2;
        public static int rejected_challenges = 3;
        public static int completed_challenge = 4;
        public static int un_completed_challenge = 5;


        public static string parseDate(long myDateTimeTicks)
        {
            const int SECOND = 1;
            const int MINUTE = 60 * SECOND;
            const int HOUR = 60 * MINUTE;
            const int DAY = 24 * HOUR;
            const int MONTH = 30 * DAY;

            var ts = new TimeSpan(DateTime.Now.Ticks - myDateTimeTicks);
            double delta = Math.Abs(ts.TotalSeconds);

            if (delta < 1 * MINUTE)
                return ts.Seconds == 1 ? "1s ago" : ts.Seconds + "s ago";

            if (delta < 2 * MINUTE)
                return "1m ago";

            if (delta < 45 * MINUTE)
                return ts.Minutes + "m ago";

            if (delta < 90 * MINUTE)
                return "1hr ago";

            if (delta < 24 * HOUR)
                return ts.Hours + "hrs ago";

            if (delta < 48 * HOUR)
                return "yesterday";

            if (delta < 30 * DAY)
                return ts.Days + "d ago";

            if (delta < 12 * MONTH)
            {
                int months = Convert.ToInt32(Math.Floor((double)ts.Days / 30));
                return months <= 1 ? "1 month ago" : months + " months ago";
            }
            else
            {
                int years = Convert.ToInt32(Math.Floor((double)ts.Days / 365));
                return years <= 1 ? "1yr ago" : years + "yrs ago";
            }
        }
    }
}