using IDARENOW_WebServer.Models;
using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Web;
using System.Web.Http;

namespace IDARENOW_WebServer.Controllers
{
    public class MessagingController : ApiController
    {
        // GET: Messaging
        public IHttpActionResult getMessagesFromId(int my_id,int from_id)
        {
            try
            {
                
                MessageFormat[] messages = (from msg in BaseClass.db.User_Messages
                                            where ((msg.User_To_ID == my_id && msg.User_From_ID == from_id) || (msg.User_To_ID == from_id && msg.User_From_ID == my_id))
                                            select new MessageFormat
                                            {
                                                msg_id = msg.Message_ID,
                                                from_id = msg.User_From_ID,
                                                to_id = msg.User_To_ID,
                                                Message_Timestamp = msg.Message_Timestamp == null?"": msg.Message_Timestamp,
                                                 Message_Description = msg.Message_Description,
                                             }).ToList().ToArray();
                if (messages.Count() > 0)
                {
                    foreach (MessageFormat item in messages)
                    {
                        if(item.from_id != my_id)
                        {
                            User_Accounts ua = BaseClass.db.User_Accounts.Where(x => x.User_ID == item.from_id).SingleOrDefault();
                            item.from_id_img = "http://"+Constants.ip_port_conn + "/Images/icons/" + ua.User_Profile_Picture;
                            item.from_id_name = ua.User_FullName;
                        }
                        else
                        {
                            item.from_id_img = "";
                        }
                        item.Message_Timestamp = BaseClass.getSweetTimeFromTimestamp(item.Message_Timestamp);
                      
                    }
                }

                return Ok(messages);
            }
            catch (Exception e)
            {
                return Ok(e.Message);
            }
        }

        // GET: Messaging for main layout
        public IHttpActionResult getAllMessages(int my_id)
        {
            try
            {

                MessageFormat2[] messages = (from msg in BaseClass.db.User_Messages
                                            where ((msg.User_To_ID == my_id || msg.User_From_ID == my_id))
                                            select new MessageFormat2
                                            {
                                                msg_id = msg.Message_ID,
                                                from_id = msg.User_From_ID,
                                                to_id = msg.User_To_ID,
                                                Message_Timestamp = msg.Message_Timestamp == null ? "" : msg.Message_Timestamp,
                                                Message_Description = msg.Message_Description,
                                            }).ToList().ToArray();
                if (messages.Count() > 0)
                {
                    List< string> sorter = new List<string>();
                    List<string> uniqueIds = new List<string>();
                    foreach (MessageFormat2 item in messages.Reverse())
                    {
                        string test = "";
                        if(item.from_id== my_id)
                        {
                            test = item.from_id + ":" + item.to_id;
                        }else if(item.to_id == my_id)
                        {
                            test = item.to_id + ":" + item.from_id;
                        }
                        if (!sorter.Contains(test))
                        {
                            uniqueIds.Add(item.msg_id+"");
                            sorter.Add(test);
                        }
                    }
                    List<MessageFormat2> myData = new List<MessageFormat2>();
                    foreach (MessageFormat2 item in messages.Reverse())
                    {
                        if (uniqueIds.Contains(item.msg_id + ""))
                        {
                            User_Accounts ua;
                            if (item.from_id != my_id)
                            {
                                 ua = BaseClass.db.User_Accounts.Where(x => x.User_ID == item.from_id).SingleOrDefault();
                            }
                            else
                            {
                                ua = BaseClass.db.User_Accounts.Where(x => x.User_ID == item.to_id).SingleOrDefault();
                            }
                            item.from_id_img    = "http://" + Constants.ip_port_conn + "/Images/icons/" + ua.User_Profile_Picture;
                            item.title_name = ua.User_FullName;

                            item.Message_Timestamp = BaseClass.getSweetTimeFromTimestamp(item.Message_Timestamp);
                            myData.Add(item);
                        }
                    }


                    return Ok(myData);

                }
                return Ok();

            }
            catch (Exception e)
            {
                return Ok(e.Message);
            }
        }


        [HttpPost]
        public IHttpActionResult postSendMsg(int my_id,int to_id,string message)
        {
            try
            {
                User_Messages msg = new User_Messages();
                msg.User_From_ID = my_id;
                msg.User_To_ID = to_id;
                msg.Message_Description = message;
                msg.Message_Timestamp = new DateTimeOffset(DateTime.Now).ToLocalTime().AddHours(Constants.GMT_positive).ToUnixTimeSeconds().ToString();
                BaseClass.db.User_Messages.Add(msg);
                BaseClass.db.SaveChanges();
                return Ok(true);
            }
            catch (Exception e)
            {
                return Ok(e.Source);
            }

        }
    }
    class MessageFormat
    {
        public int msg_id { get; set; }
        public int from_id { get; set; }
        public string from_id_name { get; set; }
        public string from_id_img { get; set; }
        public int to_id { get; set; }
        public string Message_Timestamp { get; set; }
        public string Message_Description { get; set; }
        public Nullable<bool> Message_IsRead { get; set; }
    }
    class MessageFormat2
    {
        public string title_name { get; set; }
        public int msg_id { get; set; }
        public int from_id { get; set; }
        public string from_id_img { get; set; }
        public int to_id { get; set; }
        public string Message_Timestamp { get; set; }
        public string Message_Description { get; set; }
        public Nullable<bool> Message_IsRead { get; set; }
    }

}