using IDARENOW_WebServer.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace IDARENOW_WebServer.Controllers
{
    public class BaseClass
    {
        private static AlphaRelevant_iDareNowEntities db = new AlphaRelevant_iDareNowEntities();
        public static int addNotification(int associated_id, int status)
        {
            Notification n = new Notification();
            n.user_id = associated_id;
            n.status = status;
            db.Notifications.Add(n);
            db.SaveChanges();
            return n.notification_id;

        }
    }
}