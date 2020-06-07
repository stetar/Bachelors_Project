using System;
using System.Collections.Generic;
using System.Data.SqlClient;
using System.Linq;
using System.Web;

namespace BachelorsProjectWebApp.Models
{
    public class DBMapper
    {
        string conS = @"Data Source=bach2020us.database.windows.net;Initial Catalog=bach2020DB;User ID=ubi-9;Password=4QqIrrr123;Connect Timeout=60;Encrypt=True;TrustServerCertificate=False;ApplicationIntent=ReadWrite;MultiSubnetFailover=False";

        public void AddBeacon(BeaconModel beacon)
        {
            using (var connection = new SqlConnection(conS))
            {
                connection.Open();
                var sql = "INSERT INTO Beacons(BeaconID, xPos, yPos, OfficeID) VALUES(@BeaconID, @xPos, @yPos, @OfficeID)";
                using (var cmd = new SqlCommand(sql, connection))
                {
                    cmd.Parameters.AddWithValue("@BeaconID", beacon.beaconId);
                    cmd.Parameters.AddWithValue("@xPos", beacon.xPos);
                    cmd.Parameters.AddWithValue("@yPos", beacon.yPos);
                    cmd.Parameters.AddWithValue("@OfficeID", beacon.OfficeID);

                    cmd.ExecuteNonQuery();
                }
            }
        }
        public void AddTrack(BeaconModel bm)
        {
            using (var connection = new SqlConnection(conS))
            {
                connection.Open();
                var sql = "INSERT INTO Tracks(xPos, yPos, FloorID) VALUES(@xPos, @yPos, @FloorID)";
                using (var cmd = new SqlCommand(sql, connection))
                {
                    //TODO FloorID
                    cmd.Parameters.AddWithValue("@FloorID", bm.FloorID);
                    cmd.Parameters.AddWithValue("@xPos", bm.xPos);
                    cmd.Parameters.AddWithValue("@yPos", bm.yPos);

                    cmd.ExecuteNonQuery();
                }
            }
        }


        public int AddFloor(FloorModel floor)
        {
            using (var connection = new SqlConnection(conS))
            {
                connection.Open();
                var sql = "INSERT INTO Floors(FloorPlan, FloorName, BuildingID) output INSERTED.FloorID VALUES(@FloorPlan, @FloorName, @BuildingID)";
                using (var cmd = new SqlCommand(sql, connection))
                {
                    //TODO FloorID
                    cmd.Parameters.AddWithValue("@FloorPlan", floor.Floorplan);
                    cmd.Parameters.AddWithValue("@FloorName", floor.FloorName);
                    cmd.Parameters.AddWithValue("@BuildingID", floor.BuildingID);

                    int insertedID = (int)cmd.ExecuteScalar();
                    return insertedID;
                }
            }
        }

        public FloorModel GetFloorPlanByID(int id)
        {
            FloorModel returnval = new FloorModel();
            using (var connection = new SqlConnection(conS))
            {
                connection.Open();
                var sql = "SELECT * From Floors where FloorID =" + id;
                using (var cmd = new SqlCommand(sql, connection))
                {
                    using (SqlDataReader reader = cmd.ExecuteReader())
                    {
                        while (reader.Read())
                        {
                            returnval.BuildingID = reader.GetInt32(2);
                            returnval.FloorName = reader.GetString(3);
                            returnval.Floorplan = reader.GetString(1);
                        }
                    }

                    return returnval;
                }
            }
        }

        public List<OfficeModel> GetOfficesByFloorID(int id)
        {
            List<OfficeModel> returnval = new List<OfficeModel>();
            using (var connection = new SqlConnection(conS))
            {
                connection.Open();
                var sql = "SELECT * From Offices where FloorID =" + id;
                using (var cmd = new SqlCommand(sql, connection))
                {
                    using (SqlDataReader reader = cmd.ExecuteReader())
                    {
                        while (reader.Read())
                        {
                            returnval.Add(new OfficeModel
                            {
                                OfficeID = reader.GetInt32(0),
                                OfficeNumber = reader.GetInt32(1),
                                OfficeOwner = reader.GetString(2),
                                ResearchAreas = reader.GetString(3)
                            });
                        }
                    }

                    return returnval;
                }
            }
        }

        public List<BeaconModel> GetAllBeaconsOnFloorByID(int id)
        {
            List<BeaconModel> returnval = new List<BeaconModel>();
            using (var connection = new SqlConnection(conS))
            {
                connection.Open();
                var sql = "SELECT * From Offices o inner join Beacons b on o.OfficeID = b.OfficeID where FloorID =" + id;
                using (var cmd = new SqlCommand(sql, connection))
                {
                    using (SqlDataReader reader = cmd.ExecuteReader())
                    {
                        while (reader.Read())
                        {
                            returnval.Add(new BeaconModel
                            {
                                beaconId = reader.GetString(5),
                                xPos = (float)reader.GetDouble(6),
                                yPos = (float)reader.GetDouble(7)

                            });
                        }
                    }

                    return returnval;
                }
            }
        }

        public List<TrackModel> GetAllTracksOnFloorByID(int id)
        {
            List<TrackModel> returnval = new List<TrackModel>();
            using (var connection = new SqlConnection(conS))
            {
                connection.Open();
                var sql = "SELECT * From Tracks where FloorID =" + id;
                using (var cmd = new SqlCommand(sql, connection))
                {
                    using (SqlDataReader reader = cmd.ExecuteReader())
                    {
                        while (reader.Read())
                        {
                            returnval.Add(new TrackModel
                            {
                                xPos = (float)reader.GetDouble(1),
                                yPos = (float)reader.GetDouble(2)
                            });
                        }
                    }

                    return returnval;
                }
            }
        }
    }
}