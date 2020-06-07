using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace BachelorsProjectWebApp.Models
{
    public class OfficeModel
    {
        public int OfficeID { get; set; }
        public int OfficeNumber { get; set; }
        public string OfficeOwner { get; set; }
        public string ResearchAreas { get; set; }
        public int FloorID { get; set; }

    }
}