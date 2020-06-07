using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace BachelorsProjectWebApp.Models
{
    public class EditFloorModel
    {
        public FloorModel FM { get; set; }
        public string beaconIdentifiers { get; set; }
        public string beaconPositions { get; set; }
        public string trackPositions { get; set; }

    }
    public class FloorModel
    {
        public int BuildingID { get; set; }
        public string Floorplan { get; set; }
        public string FloorName { get; set; }

    }
}