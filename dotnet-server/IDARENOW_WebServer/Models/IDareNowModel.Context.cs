//------------------------------------------------------------------------------
// <auto-generated>
//     This code was generated from a template.
//
//     Manual changes to this file may cause unexpected behavior in your application.
//     Manual changes to this file will be overwritten if the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

namespace IDARENOW_WebServer.Models
{
    using System;
    using System.Data.Entity;
    using System.Data.Entity.Infrastructure;
    
    public partial class AlphaRelevant_iDareNowEntities : DbContext
    {
        public AlphaRelevant_iDareNowEntities()
            : base("name=AlphaRelevant_iDareNowEntities")
        {
        }
    
        protected override void OnModelCreating(DbModelBuilder modelBuilder)
        {
            throw new UnintentionalCodeFirstException();
        }
    
        public virtual DbSet<Assign_Challenge> Assign_Challenge { get; set; }
        public virtual DbSet<Challenge_Type> Challenge_Type { get; set; }
        public virtual DbSet<City> Cities { get; set; }
        public virtual DbSet<Country> Countries { get; set; }
        public virtual DbSet<notification_challenge_group> notification_challenge_group { get; set; }
        public virtual DbSet<notification_types> notification_types { get; set; }
        public virtual DbSet<Post_approve_reject> Post_approve_reject { get; set; }
        public virtual DbSet<Sub_notification_type> Sub_notification_type { get; set; }
        public virtual DbSet<sysdiagram> sysdiagrams { get; set; }
        public virtual DbSet<Transaction> Transactions { get; set; }
        public virtual DbSet<User_Account_Type> User_Account_Type { get; set; }
        public virtual DbSet<User_Accounts> User_Accounts { get; set; }
        public virtual DbSet<User_Messages> User_Messages { get; set; }
        public virtual DbSet<Video_comments> Video_comments { get; set; }
        public virtual DbSet<Videos> Videos { get; set; }
    }
}
