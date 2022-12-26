using IDARENOW_WebServer.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace IDARENOW_WebServer.Controllers
{
    public class BaseClass
    {

        public static AlphaRelevant_iDareNowEntities db = new AlphaRelevant_iDareNowEntities();
        public static void assignChallengeNotification(int challenge_id)
        {
           
        
            notification_types n_types = new notification_types();
          //  n_types.notification_category = "CHALLENGE";
            n_types.notification_shown_status = 0;
            n_types.notification_category = Constants.ChallengeString;
            n_types.sub_n_type_id = Constants.initial_challenge;
            db.notification_types.Add(n_types);
            db.SaveChanges();



            notification_challenge_group n = new notification_challenge_group();
            n.n_group_id = n_types.n_type_id;
            n.challenge_id = challenge_id;
            db.notification_challenge_group.Add(n);
            db.SaveChanges();
        }

        public static string GetUniqueKey(int length)
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
        public static string getCurrentTimeStamp()
        {
            return new DateTimeOffset(DateTime.Now).ToLocalTime().AddHours(Constants.GMT_positive).ToUnixTimeSeconds().ToString();
        }
        public static string getFormattedDateTimeFromTimestamp(string timestamp,string format)
        {
            return DateTimeOffset.FromUnixTimeSeconds(((long)Convert.ToInt64(timestamp))).ToString(format);
        }
        public static string getSweetTimeFromTimestamp(string timestamp)
        {
            if (timestamp != null && timestamp != "")
            {
                string timeFromDB = BaseClass.getFormattedDateTimeFromTimestamp(timestamp, Constants.TimestamptoDTPartial);
                string monthName = System.Globalization.CultureInfo.CurrentCulture.DateTimeFormat.GetAbbreviatedMonthName(Convert.ToInt16(timeFromDB.Split(' ')[0].Split('/')[0]));
                if (Convert.ToInt16(timeFromDB.Split(' ')[0].Split('/')[1]) > 2)
                    return timeFromDB.Split(' ')[0].Split('/')[1] + " " + monthName + " " + timeFromDB.Split(' ')[1];
                else
                    return "";
            }
            else
            {
                return "";
            }

        }

    }
}