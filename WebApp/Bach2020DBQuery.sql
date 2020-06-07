Create table Beacons(
	BeaconID Varchar(100) not null,
	BuildingNumber INT,
	FloorNumber INT,
	PositionX INT,
	PositionY INT,
	primary key (BeaconID)
)


Create table Offices(
	OfficeID INT not null,
	TenantName Varchar(50) not null,
	ResearchArea Varchar(50) not null,
	Mail Varchar(50) not null,
	BeaconID Varchar(100) not null,
	primary key(OfficeID),
	FOREIGN KEY (BeaconID) REFERENCES Beacons(BeaconID)
)
