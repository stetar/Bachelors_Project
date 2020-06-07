using BachelorsProjectWebApp.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;

namespace BachelorsProjectWebApp.Controllers
{

    public class HomeController : Controller
    {
        //Instantiating our DBMapper class. It contains CRUD operations for our database.
        DBMapper dbmapper = new DBMapper();
        public ActionResult Index()
        {
            return View();
        }
        /// <summary>
        /// Hit when we navigate to EditPlan. Loads the floor plan based on ID.
        /// </summary>
        /// <param name="id">Floor plan ID</param>
        /// <returns></returns>
        public ActionResult EditPlan(int id)
        {
            var fm = dbmapper.GetFloorPlanByID(id);
            List<OfficeModel> offices = dbmapper.GetOfficesByFloorID(id);

            ViewBag.imgurl = fm.Floorplan;
            ViewBag.FloorID = id;
            return View(offices);
        }

        /// <summary>
        /// Hit when we save changes on EditPlan. Support for only adding beacons or only adding tracks not implemented.
        /// </summary>
        /// <param name="bm">The changes done on EditPlan is saved to this object.</param>
        /// <returns></returns>
        [HttpPost]
        public ActionResult EditPlan(BeaconModel bm)
        {
            var beacons = new List<BeaconModel>();
            var tracks = new List<BeaconModel>();
            
            string[] seperator = { " " };
            string[] posSeperator = { "," };


            try
            {
                string[] beaconIdList = bm.beaconIDString.Split(seperator, StringSplitOptions.RemoveEmptyEntries);

                string[] beaconPosList = bm.beaconPosString.Split(seperator, StringSplitOptions.RemoveEmptyEntries);
                string[] officeids = bm.OfficeIDString.Split(seperator, StringSplitOptions.RemoveEmptyEntries);

                string[] trackPosList = bm.trackPosString.Split(seperator, StringSplitOptions.RemoveEmptyEntries);
                for (int i = 0; i < beaconPosList.Length; i++)
                {
                    string[] xyPos = beaconPosList[i].Split(posSeperator, StringSplitOptions.RemoveEmptyEntries);
                    float floatx = float.Parse(xyPos[0]);
                    float floaty = float.Parse(xyPos[1]);
                    beacons.Add(new BeaconModel { OfficeID = int.Parse(officeids[i]), beaconId = beaconIdList[i], xPos = floatx, yPos = floaty });
                }
                for (int i = 0; i < trackPosList.Length; i++)
                {
                    string[] xyPos = trackPosList[i].Split(posSeperator, StringSplitOptions.RemoveEmptyEntries);
                    float floatx = float.Parse(xyPos[0]);
                    float floaty = float.Parse(xyPos[1]);
                    tracks.Add(new BeaconModel { FloorID=bm.FloorID, xPos = floatx, yPos = floaty });
                }

                foreach (var beacon in beacons)
                {
                    dbmapper.AddBeacon(beacon);
                }
                foreach (var track in tracks)
                {
                    dbmapper.AddTrack(track);
                }
            }
            catch (Exception)
            {

            }

            ViewBag.bString = bm.beaconPosString;

            return RedirectToAction("EditPlan", new { id = bm.FloorID });
        }

        
        /// <summary>
        /// Hit when using one of the search bars.
        /// </summary>
        /// <param name="Search">The input of the searchbar</param>
        /// <returns></returns>
        public ActionResult FindOffice(string Search)
        {
            ViewBag.searchid = Search;
            return View("FindOffice");
        }
        /// <summary>
        /// Navigates to a paper page, which is not done at all.
        /// </summary>
        /// <param name="id">Id of the section clicked</param>
        /// <returns></returns>
        public ActionResult Papers(string id)
        {
            ViewBag.papers = id;
            return View();
        }

        /// <summary>
        /// Simple page with uploadfloorplan form
        /// </summary>
        /// <returns></returns>
        public ActionResult UploadFloorPlan()
        {
            return View();
        }
        /// <summary>
        /// Hit when clicking "Upload floor plan" on the above page.
        /// </summary>
        /// <param name="fm">contains image url and name</param>
        /// <returns></returns>
        [HttpPost]
        public ActionResult UploadFloorPlan(FloorModel fm)
        {
            //Something database get id when execute query
            
            var floorplanid = dbmapper.AddFloor(new FloorModel {BuildingID = 1, Floorplan=fm.Floorplan, FloorName=fm.FloorName });

            return RedirectToAction("EditPlan", new { id = floorplanid } );
        }
        /// <summary>
        /// Loads beacons and tracks from DB, and passes them with imgurl to the view.
        /// </summary>
        /// <param name="destination">Is a string that needs to be split and parsed to ints.</param>
        /// <returns></returns>
        public ActionResult NavigationDemo(string destination)
        {
            string[] seperator = { "," };
            string[] urlpars = destination.Split(seperator, StringSplitOptions.RemoveEmptyEntries);
            var nm = new NavigationModel();
            nm.beaconlist = dbmapper.GetAllBeaconsOnFloorByID(int.Parse(urlpars[1]));
            nm.tracklist = dbmapper.GetAllTracksOnFloorByID(int.Parse(urlpars[1]));
            var fm = dbmapper.GetFloorPlanByID(int.Parse(urlpars[1]));
            ViewBag.imgurl = fm.Floorplan;
            ViewBag.demo_id = urlpars[0];
            return View(nm);
        }
        /// <summary>
        /// Loads beacons and tracks from DB, and passes them with imgurl to the view.
        /// </summary>
        /// <param name="destination">Is a string that needs to be split and parsed to ints.</param>
        /// <returns></returns>
        public ActionResult Navigation(string destination)
        {
            string[] seperator = { "," };
            string[] urlpars = destination.Split(seperator, StringSplitOptions.RemoveEmptyEntries);
            var nm = new NavigationModel();
            nm.beaconlist = dbmapper.GetAllBeaconsOnFloorByID(int.Parse(urlpars[1]));
            nm.tracklist = dbmapper.GetAllTracksOnFloorByID(int.Parse(urlpars[1]));
            var fm = dbmapper.GetFloorPlanByID(int.Parse(urlpars[1]));
            ViewBag.imgurl = fm.Floorplan;
            ViewBag.demo_id = urlpars[0];
            return View(nm);
        }
    }
}