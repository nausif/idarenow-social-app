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
    using System.Collections.Generic;
    
    public partial class notification_types
    {
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Usage", "CA2214:DoNotCallOverridableMethodsInConstructors")]
        public notification_types()
        {
            this.notification_challenge_group = new HashSet<notification_challenge_group>();
        }
    
        public int n_type_id { get; set; }
        public string notification_category { get; set; }
        public Nullable<int> sub_n_type_id { get; set; }
        public Nullable<int> notification_shown_status { get; set; }
    
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Usage", "CA2227:CollectionPropertiesShouldBeReadOnly")]
        public virtual ICollection<notification_challenge_group> notification_challenge_group { get; set; }
        public virtual Sub_notification_type Sub_notification_type { get; set; }
    }
}