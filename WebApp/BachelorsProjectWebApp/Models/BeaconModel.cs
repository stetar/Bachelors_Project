using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace BachelorsProjectWebApp.Models
{
    public class BeaconModel
    {
        public int FloorID { get; set; }
        public int OfficeID { get; set; }
        //string because we have a list on the javascript side and formats it to a string to send it.
        public string OfficeIDString { get; set; }
        public string beaconPosString { get; set; }
        public string trackPosString { get; set; }
        public string beaconIDString { get; set; }
        public string beaconId { get; set; }
        public float xPos { get; set; }
        public float yPos { get; set; }

    }
}