using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace BachelorsProjectWebApp.Models
{
    public class NavigationModel
    {
        public List<BeaconModel> beaconlist{ get; set; }
        public List<TrackModel> tracklist { get; set; }
    }
}