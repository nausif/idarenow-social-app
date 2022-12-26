using IDARENOW_WebServer.Models;
using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;

namespace IDARENOW_WebServer.Controllers
{
    public class EditProfileController : ApiController
    {
        private AlphaRelevant_iDareNowEntities db = new AlphaRelevant_iDareNowEntities();

       
        [HttpGet]
        public IHttpActionResult getProfileData(int user_id)
        {
            EditProfile editProfile = (from user_Accounts in BaseClass.db.User_Accounts
                                       where user_Accounts.User_ID == user_id
                                       select new EditProfile
                                       {
                                           profile_id = user_Accounts.User_ID,
                                           full_name = user_Accounts.User_FullName,
                                           email = user_Accounts.User_Email,
                                           birthday = user_Accounts.User_Birthdate.ToString(),
                                           country_id = (int) user_Accounts.User_Country_ID,
                                           city_id = (int) user_Accounts.User_City_ID,
                                           country_name = BaseClass.db.Countries.Where(x => x.Country_ID == user_Accounts.User_Country_ID).Select(y => y.Country_Name).FirstOrDefault(),
                                           city_name = BaseClass.db.Cities.Where(x => x.City_ID == user_Accounts.User_City_ID).Select(y => y.City_Name).FirstOrDefault(),
                                           
                                   }).FirstOrDefault();
             editProfile.countries = (from country in db.Countries
                                     select new Countries
                                     {
                                         country_ID = country.Country_ID,
                                         country_Name = country.Country_Name
                                     }).ToArray();

            editProfile.cities = (from City in db.Cities
                                     where City.Country_ID == editProfile.country_id 
                                     select new Cities
                                     {
                                         city_ID = City.City_ID,
                                         city_Name = City.City_Name
                                     }).ToArray();
            if (editProfile.birthday != "")
                editProfile.birthday = Convert.ToDateTime(editProfile.birthday).ToString("dd/MM/yyyy");
            else editProfile.birthday = null;
            return Ok(editProfile);
        }

        [HttpGet]
        public IHttpActionResult getSelectedCitiesFromCountry(int country_id)
        {
            List<Cities> cities = (from City in db.Cities
                                   where City.Country_ID == country_id
                                   select new Cities
                                   {
                                       city_ID = City.City_ID,
                                       city_Name = City.City_Name
                                   }).ToList();
            return Ok(cities);
        }

        [HttpPost]
        public void postUpdateProfile([FromBody] EditProfile ed)    
        {
            if (ed != null)
            {
                var ua = db.User_Accounts.SingleOrDefault(x => x.User_ID == ed.profile_id);
                ua.User_FullName = ed.full_name;
                ua.User_Email = ed.email;
                if(ed.birthday!="")
                    ua.User_Birthdate = DateTime.ParseExact(ed.birthday,"dd/MM/yyyy", null);

                ua.User_Country_ID = ed.country_id;
                ua.User_City_ID = ed.city_id;
                db.SaveChanges();
            }
        }

        [HttpPost]
        public Boolean postChangePassword(int user_id,string current_pass, string new_pass)
        {
            var ua = db.User_Accounts.SingleOrDefault(x => x.User_ID == user_id);
            if (ua != null)
            {
                if(current_pass == ua.User_Password)
                {
                    ua.User_Password = new_pass;
                    db.SaveChanges();
                    return true;
                }
                else
                {
                    return false;
                }
            }
            return false;
        }


    }
    

    public class Countries
    {
        public int country_ID;
        public string country_Name;
    }

    public class Cities
    {
        public int city_ID;
        public string city_Name;
    }
    public class EditProfile
    {
        public int profile_id;
        public string full_name;
        public string email;
        public String birthday;
        public String country_name;
        public int? country_id;
        public int? city_id;
        public String city_name;
        public Countries[] countries;
        public Cities[] cities;
    }
    
    
}
