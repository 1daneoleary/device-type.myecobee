/**
 *  ecobeeSetZonesWithSchedule
 *
 *  Copyright 2015 Yves Racine
 *  linkedIn profile: ca.linkedin.com/pub/yves-racine-m-sc-a/0/406/4b/
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
definition(
	name: "ecobeeSetZoneWithSchedule",
	namespace: "yracine",
	author: "Yves Racine",
	description: "Enables Heating/Cooling Zoned Solutions based on your ecobee schedule(s)- coupled with z-wave vents (optional) for better temp settings control throughout your home",
	category: "My Apps",
	iconUrl: "https://s3.amazonaws.com/smartapp-icons/Partner/ecobee.png",
	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Partner/ecobee@2x.png"
)



preferences {

	page(name: "generalSetupPage")
	page(name: "roomsSetupPage")
	page(name: "zonesSetupPage")
	page(name: "schedulesSetupPage")
	page(name: "NotificationsPage")
	page(name: "roomsSetup")
	page(name: "zonesSetup")
	page(name: "schedulesSetup")
}

def generalSetupPage() {

	dynamicPage(name: "generalSetupPage", uninstall: true, nextPage: roomsSetupPage) {
		section("About") {
			paragraph "ecobeeSetZoneWithSchedule, the smartapp that enables Heating/Cooling Zoned Solutions based on your ecobee schedule(s)- coupled with z-wave vents (optional) for better temp settings control throughout your home"
			paragraph "Version 0.9\n\n" +
				"If you like this app, please support the developer via PayPal:\n\nyracine@yahoo.com\n\n" +
				"Copyright©2015 Yves Racine"
			href url: "http://github.com/yracine", style: "embedded", required: false, title: "More information...",
				description: "http://github.com/yracine/device-type.myecobee/blob/master/README.md"
		}
		section("Main ecobee thermostat at home") {
			input (name:"thermostat", type: "capability.thermostat", title: "Which main ecobee thermostat?")
		}
		section("Rooms count") {
			input (name:"roomsCount", title: "Rooms count (max=16)?", type: "number",refreshAfterSelection: true)
		}
		section("Zones count") {
			input (name:"zonesCount", title: "Zones count (max=8)?", type:"number",refreshAfterSelection: true)
		}
		section("Schedules count") {
			input (name:"schedulesCount", title: "Schedules count (max=12)?", type: "number",refreshAfterSelection: true)
		}
		section("Set your main thermostat to [Away,Present] based on all Room Motion Sensors [default=false] ") {
			input (name:"setAwayOrPresentFlag", title: "Set Main thermostat to [Away,Present]?", type:"Boolean",
				description:"optional", metadata: [values: ["true", "false"]],required:false)
		}
		section("Outdoor temp Sensor used for adjustment [optional]") {
			input (name:"outTempSensor", type:"capability.temperatureMeasurement", required: false,
					description:"optional")				            
		}
		section("What do I use for the Master on/off switch to enable/disable processing? [optional]") {
			input (name:"powerSwitch", type:"capability.switch", required: false)
		}
		if (thermostat) {
			section {
				href(name: "toRoomPage", title: "Room Setup", page: "roomsSetupPage")
				href(name: "toZonePage", title: "Zone Setup", page: "zonesSetupPage")
				href(name: "toSchedulePage", title: "Schedule Setup", page: "schedulesSetupPage")
				href(name: "toNotificationsPage", title: "Notifications Setup", page: "NotificationsPage")
			}                
		}
	}
}






def roomsSetupPage() {

	dynamicPage(name: "roomsSetup", title: "Rooms Setup", uninstall: true, nextPage: zonesSetupPage) {

		for (int indiceRoom = 1;
			((indiceRoom <= settings.roomsCount) && (indiceRoom <= 16)); indiceRoom++) {
            			section("Room ${indiceRoom} Setup") {
				input "roomName${indiceRoom}", title: "Room Name", "string"
			}
			section("Room ${indiceRoom}-Thermostat [optional]") {
				input "roomTstat${indiceRoom}", title: "Room thermostat to be set", "capability.thermostat", required: false

			}
			section("Room ${indiceRoom}-TempSensor [optional]") {
				input "tempSensor${indiceRoom}", title: "Temp sensor for better temp adjustment", "capability.temperatureMeasurement", required: false

			}
			section("Room ${indiceRoom}-Vents Setup [optional]")  {
				for (int j = 1;(j <= 5); j++)  {
					input "ventSwitch${j}${indiceRoom}", title: "Vent switch no ${j} in room", "capability.switch", required: false
				}           
			}           
			section("Room ${indiceRoom}-MotionSensor [optional]") {
				input "motionSensor${indiceRoom}", title: "Motion sensor (if any) to detect if room is occupied", "capability.motionSensor", required: false

			}
			section("Room ${indiceRoom}-Do temp adjustment based on avg temp calculation when occupied room only [optional]") {
				input "needOccupiedFlag${indiceRoom}", title: "Will do temp adjustement only when Occupied [default=false]", "Boolean", metadata: [values: ["true", "false"]], required: false

			}
			section("Room ${indiceRoom}-Do temp adjustment with this occupied's threshold [optional]") {
				input "residentsQuietThreshold${indiceRoom}", title: "Threshold in minutes for motion detection [default=15 min]", "number", required: false

			}
			section() {
				paragraph "**** DONE FOR ROOM ${indiceRoom} **** "

			}                
		} /* end for */
		section {
			href(name: "toGeneralSetupPage", title: "Back to General Setup Page", page: "generalSetupPage")
		}

	}

}
/*
def roomHrefDescription(i) {
	def description ="Room no${i} " 
	if (settings."roomName${i}" !=null) {
		description += settings."roomName${i}"		    
    }
	return description
}

def roomPageState(i) {

	if (settings."roomName${i}" != null) {
		return 'complete'
	} else {
		return 'incomplete'
	}

}

def roomHrefTitle(i) {
	def title = "Room ${i}"
	return title
}

def parse(description) {
    ...
    def msg = parseLanMessage(description)

    def headersAsString = msg.header // => headers as a string
    def headerMap = msg.headers      // => headers as a Map
    def body = msg.body              // => request body as a string
    def status = msg.status          // => http status code of the response
    def json = msg.json              // => any JSON included in response body, as a data structure of lists and maps
    def xml = msg.xml                // => any XML included in response body, as a document tree structure
    def data = msg.data              // => either JSON or XML in response body (whichever is specified by content-type header in response)
}

def roomsSetupPage() {


	def host = getHostAddress()
	def port = host.split(":")[1]
	def path = "/idHashString/devices"

	def hubAction = new physicalgraph.device.HubAction(
		method: "GET",
		path: path,
		headers: [HOST:host]
	)
	hubAction.options = [outputMsgToS3:true]
	hubAction
    
	dynamicPage(name: "roomsSetupPage", title: "Rooms Setup",nextPage: zonesSetupPage) {
		section("Rooms") {
			for (int i = 1; i <= settings.roomsCount; i++) {
				href(name: "toRoomPage$i", page: "roomsSetup", params: [indiceRoom: i],  description: roomHrefDescription(i), title: roomHrefTitle(i), state: roomPageState(i) )
			}		
		}
		
		section {
			href(name: "toGeneralSetupPage", title: "Back to General Setup Page", page: "generalSetupPage")
		}
        
	}        
}        

def roomsSetup(params) {
	def indiceRoom = params?.indiceRoom?.intValue()
	log.debug "roomsSetup> indiceRoom=${indiceRoom}"
    
	dynamicPage(name: "roomsSetup", title: "Rooms Setup") {
		section("Room ${indiceRoom} Setup") {
			input (name: "roomName${indiceRoom}", title: "Room Name",  type: "text",  
				defaultValue:settings."roomName${indiceRoom}")
		}
        
		section("Room ${indiceRoom}-Thermostat [optional]") {
			input (name: "roomTstat${indiceRoom}", title: "Room thermostat to be set", type: "capability.thermostat", 
				required: false, defaultValue:settings."roomTstat${indiceRoom}")
                
                
		}
		section("Room ${indiceRoom}-TempSensor [optional]") {
			input (name: "tempSensor${indiceRoom}", title: "Temp sensor to be used for better temp adjustment",  
				type: "capability.temperatureMeasurement", defaultValue:settings."tempSensor${indiceRoom}", required:false)
		}
            
		section("Room ${indiceRoom}-Vent Switches [optional]") {
			input (name: "ventSwitch1${indiceRoom}" ,title: "Vent switch no1 in room",  
					type: "capability.switch", defaultValue: settings."ventSwitch1${indiceRoom}", required: false)
			input (name: "ventSwitch2${indiceRoom}" ,title: "Vent switch no2 in room",  
					type: "capability.switch", defaultValue: settings."ventSwitch2${indiceRoom}", required: false)

			input (name: "ventSwitch3${indiceRoom}" ,title: "Vent switch no3 in room",  
					type: "capability.switch", defaultValue: settings."ventSwitch3${indiceRoom}", required: false)

			input (name: "ventSwitch4${indiceRoom}" ,title: "Vent switch no4 in room",  
					type: "capability.switch", defaultValue: settings."ventSwitch4${indiceRoom}", required: false)
			input (name: "ventSwitch5${indiceRoom}" ,title: "Vent switch no5 in room",  
					type: "capability.switch", defaultValue: settings."ventSwitch5${indiceRoom}", required: false)
                    
		}
        
		section("Room ${indiceRoom}-MotionSensor [optional]") {
			input (name: "motionSensor${indiceRoom}", title: "Motion sensor to be used to detect if room is occupied", 
				type: "capability.motionSensor", defaultValue:settings."motionSensor${indiceRoom}",required: false)
		}
        
		section("Room ${indiceRoom}-Do temp adjustment based on avg temp calculation when occupied room only") {
			input (name: "needOccupiedFlag${indiceRoom}", title: "Will do temp adjustement only when Occupied [default=false]", 
				type: "Boolean",metadata: [values: ["true", "false"]],  
				defaultValue:settings."needOccupiedFlag${indiceRoom}",required: false)
                
		}
		section("Room ${indiceRoom}-Do temp adjustment with this occupied's threshold") {
			input (name: "residentsQuietThresh${indiceRoom}", title: "Threshold in minutes for motion detection [default=15 min]",
				type: "number",required: false,
 				defaultValue: settings."residentsQuietThresh${indiceRoom}")
                
		}                
		
		section {
			href(name: "toRoomsSetupPage", title: "Back to Rooms Setup Page", page: "roomsSetupPage")
		}

	}

}

*/

def zoneHrefDescription(i) {
	def description ="Zone no ${i} "

	if (settings."zoneName${i}" !=null) {
		description += settings."zoneName${i}"		    	
	}
	return description
}

def zonePageState(i) {

	if (settings."zoneName${i}" != null) {
		return 'complete'
	} else {
		return 'incomplete'
	}
  
}

def zoneHrefTitle(i) {
	def title = "Zone ${i}"
	return title
}

def zonesSetupPage() {

	dynamicPage(name: "zonesSetupPage", title: "Zones Setup", nextPage: schedulesSetupPage) {
		section("Zones") {
			for (int i = 1; i <= settings.zonesCount; i++) {
				href(name: "toZonePage$i", page: "zonesSetup", params: [indiceZone: i], required:false, description: zoneHrefDescription(i), title: zoneHrefTitle(i), state: zonePageState(i) )
			}
		}            
		section {
			href(name: "toGeneralSetupPage", title: "Back to General Setup Page", page: "generalSetupPage")
		}
	}
}        

def zonesSetup(params) {

	def rooms = []
	for (i in 1..settings.roomsCount) {
		def key = "roomName$i"
		def room = "${i}:${settings[key]}"
		rooms = rooms + room
	}
	log.debug "rooms: $rooms"

	def indiceZone=0   

	// Assign params to indiceZone.  Sometimes parameters are double nested.
	if (params?.indiceZone || params?.params?.indiceZone) {

		if (params.indiceZone) {
			indiceZone = params.indiceZone
		} else {
			indiceZone = params.params.indiceZone
		}
	}    
	indiceZone=indiceZone.intValue()
	log.debug "zonesSetup> indiceZone=${indiceZone}"
	dynamicPage(name: "zonesSetup", title: "Zones Setup") {
		section("Zone ${indiceZone} Setup") {
			input (name:"zoneName${indiceZone}", title: "Zone Name", type: "text",
				defaultValue:settings."zoneName${indiceZone}")
		}
		section("Zone ${indiceZone}-Included rooms") {
			input (name:"includedRooms${indiceZone}", title: "Rooms included in the zone", type: "enum",
				options: rooms,
				multiple: true,
				defaultValue:settings."includedRooms${indiceZone}")
		}
		section {
			href(name: "toZonesSetupPage", title: "Back to Zones Setup Page", page: "zonesSetupPage")
		}
	}            
}

def scheduleHrefDescription(i) {
	def description ="Schedule no ${i} " 
	if (settings."scheduleName${i}" !=null) {
		description += settings."scheduleName${i}"		    
    }
	return description
}

def schedulePageState(i) {

	if (settings."scheduleName${i}"  != null) {		    
		return 'complete'
	} else {
		return 'incomplete'
	}	
    
}

def scheduleHrefTitle(i) {
	def title = "Schedule ${i}"
	return title
}

def schedulesSetupPage() {
	dynamicPage(name: "schedulesSetupPage", title: "Schedule Setup", nextPage: NotificationsPage) {
		section("Schedules") {
			for (int i = 1; i <= settings.schedulesCount; i++) {
				href(name: "toSchedulePage$i", page: "schedulesSetup", params: [indiceSchedule: i],required:false, description: scheduleHrefDescription(i), title: scheduleHrefTitle(i), state: schedulePageState(i) )
			}
		}            
		section {
			href(name: "toGeneralSetupPage", title: "Back to General Setup Page", page: "generalSetupPage")
		}
	}
}        

def schedulesSetup(params) {
    
	def ecobeePrograms=[]
	// try to get the thermostat programs list (ecobee)
	try {
		ecobeePrograms = thermostat?.currentClimateList.toString().minus('[').minus(']').tokenize(',')
		ecobeePrograms.sort()        
	} catch (e) {
		log.debug("Not able to get the list of climates (ecobee), exception $e")    	
	}    
    
    
	log.debug "programs: $ecobeePrograms"

	def zones = []
    
	for (i in 1..settings.zonesCount) {
		def key = "zoneName$i"
		def zoneName =  "${i}:${settings[key]}"   
		zones = zones + zoneName
	}
	log.debug "zones: $zones"

	
	def enumModes=[]
	location.modes.each {
		enumModes << it.name
	}    
    
	def indiceSchedule=0
	// Assign params to indiceSchedule.  Sometimes parameters are double nested.
	if (params?.indiceSchedule || params?.params?.indiceSchedule) {

		if (params.indiceSchedule) {
			indiceSchedule = params.indiceSchedule
		} else {
			indiceSchedule = params.params.indiceSchedule
		}
	}    
	indiceSchedule=indiceSchedule.intValue()
	log.debug "scheduleSetup> indiceSchedule=${indiceSchedule}"

	dynamicPage(name: "schedulesSetup", title: "Schedule Setup") {
		section("Schedule ${indiceSchedule} Setup") {
			input (name:"scheduleName${indiceSchedule}", title: "Schedule Name", type: "text",
            	defaultValue:settings."scheduleName${indiceSchedule}")
		}
		section("Schedule ${indiceSchedule}-Select the program schedule(s) at ecobee thermostat for the included zone(s)") {
			input (name:"givenClimate${indiceSchedule}", type:"enum", title: "Which ecobee program? ", options: ecobeePrograms,  
				defaultValue:settings."givenClimate${indiceSchedule}")
		}
		section("Schedule ${indiceSchedule}-Included zones") {
			input (name:"includedZones${indiceSchedule}", title: "Zones included in this schedule", type: "enum",
				defaultValue:settings."includedZones${indiceSchedule}",
				options: zones,
 				multiple: true)
		}
		section("Schedule ${indiceSchedule}-More or Less Heat/Cool Threshold in the selected zone(s) based on outdoor temp Sensor [optional]") {
			input (name:"moreHeatThreshold${indiceSchedule}", type:"decimal", title: "Outdoor temp's threshold for more heating", required: false,
				defaultValue:settings."moreHeatThreshold${indiceSchedule}")			                
			input (name:"moreCoolThreshold${indiceSchedule}", type:"decimal", title: "Outdoor temp's threshold for more cooling",required: false,
				,defaultValue:settings."moreCoolThreshold${indiceSchedule}")
			input (name:"lessHeatThreshold${indiceSchedule}", type:"decimal", title: "Outdoor temp's threshold for less heating", required: false,
				defaultValue:settings."lessHeatThreshold${indiceSchedule}")			                
			input (name:"lessCoolThreshold${indiceSchedule}", type:"decimal", title: "Outdoor temp's threshold for less cooling",required: false,
				,defaultValue:settings."lessCoolThreshold${indiceSchedule}")
		}
		section("Schedule ${indiceSchedule}-Max Temp Adjustment at the main thermostat based on temp Sensors [indoor&outdoor]") {
			input (name:"givenMaxTempDiff${indiceSchedule}", type:"decimal",  title: "Max Temp adjustment (default= +/-5°F/2°C)", required: false,
				defaultValue:settings."givenMaxTempDiff${indiceSchedule}")
		}
		section("Schedule ${indiceSchedule}-Set Fan Mode [optional]") {
			input (name:"fanMode${indiceSchedule}", type:"enum", title: "Set Fan Mode ['on', 'auto', 'circulate']", metadata: [values: ["on", "auto", "circulate"]], required: false,
				defaultValue:settings."fanMode${indiceSchedule}")
			input (name:"moreFanThreshold${indiceSchedule}", type:"decimal", title: "Outdoor temp's threshold for Fan Mode", required: false,
				defaultValue:settings."moreFanThreshold${indiceSchedule}")			                
			input (name:"fanModeForThresholdOnlyFlag${indiceSchedule}", type:"Boolean",  title: "Set Fan Mode only when Threshold is reached(default=false)", 
				required: false, defaultValue:settings."fanModeForThresholdOnlyFlag${indiceSchedule}")
		}
		section("Schedule ${indiceSchedule}-Set Room Thermostats Only Indicator [optional]") {
			input (name:"setRoomThermostatsOnlyFlag${indiceSchedule}", type:"Boolean", title: "Set room thermostats only [default=false,main & room thermostats setpoints are set]", metadata: [values: ["true", "false"]], 
				required: false, defaultValue:settings."setRoomThermostatsOnlyFlag${indiceSchedule}")
		}
		section("Schedule ${indiceSchedule}-Set for specific mode(s) [default=all]")  {
			input (name:"selectedMode${indiceSchedule}", type:"enum", title: "Choose Mode", options: enumModes, required: false, multiple:true,
				defaultValue:settings."selectedMode${indiceSchedule}")
		}
		section {
			href(name: "toSchedulesSetupPage", title: "Back to Schedules Setup Page", page: "schedulesSetupPage")
		}
	}        
}

def NotificationsPage() {
	dynamicPage(name: "NotificationsPage", title: "Other Options", install: true) {
		section("Notifications") {
			input "sendPushMessage", "enum", title: "Send a push notification?", metadata: [values: ["Yes", "No"]], required: false
			input "phone", "phone", title: "Send a Text Message?", required: false
		}
		section("Detailed Notifications") {
			input "detailedNotif", "Boolean", title: "Detailed Notifications?", metadata: [values: ["true", "false"]], required:
				false
		}
		section([mobileOnly: true]) {
			label title: "Assign a name for this SmartApp", required: false
		}
		section {
			href(name: "toGeneralSetupPage", title: "Back to General Setup Page", page: "generalSetupPage")
		}
	}
}



def installed() {
	initialize()
}

def updated() {
	unsubscribe()
	unschedule()
	initialize()
}

def offHandler(evt) {
	log.debug "$evt.name: $evt.value"
}

def onHandler(evt) {
	log.debug "$evt.name: $evt.value"
	setZoneSettings()
}


def initialize() {

	if (powerSwitch) {
		subscribe(powerSwitch, "switch.off", offHandler, [filterEvents: false])
		subscribe(powerSwitch, "switch.on", onHandler, [filterEvents: false])
	}
	// Initialize state variables
    
	state.lastScheduleLastName=""
	state.scheduleHeatingSetpoint=0  
	state.scheduleCoolingSetpoint=0    
	state.setPresentOrAway='present'
    
	Integer delay =5 				// wake up every 5 minutes to apply zone settings if any
	log.debug "Scheduling setZoneSettings every ${delay} minutes to check for zone settings to be applied"

	runEvery5Minutes(setZoneSettings)

	subscribe(app, appTouch)
	subscribe(thermostat, "programScheduleName", setZoneSettings)    
}

def appTouch(evt) {
	setZoneSettings()
}



def setZoneSettings() {

	thermostat.poll()
	def scheduleProgramName = thermostat.currentClimateName
    
	if (powerSwitch?.currentSwitch == "off") {
		if (detailedNotif == 'true') {
			send("ecobeeSetZoneWithSchedule>${powerSwitch.name} is off, schedule processing on hold...")
		}
		return
	}

	def currTime = now()

	def ventSwitchesOn = []
	for (int i = 1;((i <= settings.schedulesCount) && (i <= 12)); i++) {
        
        
		def key = "scheduleName$i"
		def scheduleName = settings[key]
        
        
		log.debug "setZoneSettings>found schedule=${scheduleName}, scheduled program at ecobee=$scheduleProgramName..."
		key = "selectedMode$i"
		def selectedModes = settings[key]

		Boolean foundMode=false        
		selectedModes.each {
        
			if (it==location.mode) {
				foundMode=true            
			}            
		}        
        
		if ((selectedModes != null) && (!foundMode)) {
        
			log.debug "setZoneSettings>schedule=${scheduleName} does not apply,location.mode= $location.mode, selectedModes=${selectedModes},foundMode=${foundMode}, continue"
			continue			
		}
		key = "givenClimate$i"
		def selectedClimate=settings[key]
		if ((selectedClimate==scheduleProgramName) && (scheduleName != state.lastScheduleName)) {
        
			// let's set the given zone(s) for this program schedule
            
            
			log.debug "setZoneSettings>now applying ${scheduleName}, scheduled program is now ${scheduleProgramName}"

			state.lastScheduleName = scheduleName
                
			if (detailedNotif == 'true') {
				send("ScheduleTstatZones>running schedule ${scheduleName},about to set zone settings as requested")
			}
        
			// set the zoned vent switches to 'on'
			def ventSwitchesZoneSet= control_vent_switches_in_zone(i)
			log.debug "setZoneSettings>schedule ${scheduleName},list of Vents turned 'on'= ${ventSwitchesZoneSet}"
			// adjust the temperature at the thermostat(s)
			adjust_thermostat_setpoint_in_zone(i)
 			ventSwitchesOn = ventSwitchesOn + ventSwitchesZoneSet              
		} else if (state.lastScheduleName == scheduleName) {
			// We're in the middle of a schedule run
        
			def setAwayOrPresent = (setAwayOrPresentFlag)?:'false'
			Boolean isResidentPresent=true
            
			if (setAwayOrPresent=='true') {
	            
				isResidentPresent=verify_presence_based_on_motion_in_rooms()
				if (isResidentPresent) {            

					if (state.setPresentOrAway != 'present') {
						if (detailedNotif == 'true') {
							send("ScheduleTstatZones>schedule ${scheduleName}: trying to set ${thermostat} to 'present' mode")
						}
						set_main_tstat_to_AwayOrPresent('present')
					}
				} else {
					if (state.setPresentOrAway != 'away') {
						if (detailedNotif == 'true') {
							send("ScheduleTstatZones>schedule ${scheduleName}: trying to set ${thermostat} to 'away' mode")
						}
						set_main_tstat_to_AwayOrPresent('away')
					}                
				}
			}            
			if (isResidentPresent) {
            
				// let's adjust the thermostat's temp & mode settings according to outdoor temperature
				adjust_tstat_for_more_less_heat_cool(i)
				// will override the fan settings if required (ex. more Fan Threshold is set)
				set_fan_mode(i)
            
			}        
			// let's adjust the vent settings according to desired Temp
            
			// let's adjust the thermostat's temp & mode settings according to outdoor temperature
			adjust_tstat_for_more_less_heat_cool(i)
		}

	} /* end for */ 	
	if (ventSwitchesOn != []) {
		log.debug "setZoneSettings>list of Vents turned on= ${ventSwitchesOn}"
		turn_off_all_other_vents(ventSwitchesOn)
	}
	log.debug "End of Fcn"
}

private def isRoomOccupied(sensor, indiceRoom) {
	def key = "residentsQuietThreshold$indiceRoom"
	def threshold = (settings[key]) ?: 15 // By default, the delay is 15 minutes 

	key = "roomName$indiceRoom"
	def roomName = settings[key]

	def result = false
	def t0 = new Date(now() - (threshold * 60 * 1000))
	def recentStates = sensor.statesSince("motion", t0)
	if (recentStates.find {it.value == "active"}) {
		log.debug "isRoomOccupied>room ${roomName} has been occupied, motion was detected at sensor ${sensor} in the last ${threshold} minutes"
		result = true
	}
	log.debug "isRoomOccupied>result = $result"
	return result
}

private def verify_presence_based_on_motion_in_rooms() {

	def result=false
	for (i in 1..roomsCount) {

		def key = "roomName$i"
		def roomName = settings[key]
		key = "motionSensor$i"
		def motionSensor = settings[key]
		if (motionSensor != null) {

			if (isRoomOccupied(motionSensor,i)) {
				log.debug("verify_presence_based_on_motion>in ${roomName},presence detected, return true")
				result=true
			}                
		}
	} /* end for */        
	return result
}

private def set_main_tstat_to_AwayOrPresent(mode) {

	try {
    
		if  (mode == 'away') {
			thermostat.away()
            
		} else if (mode == 'present') {	
			thermostat.present()
		}
            
		if (detailedNotif == 'true') {
			send("ecobeeSetZoneWithSchedule>set main thermostat ${thermostat} to ${mode} mode based on motion in all rooms")
		}
		state.setPresentOrAway=mode    // set a state for further checking later
	}    
	catch (e) {
		log.error("set_tstat_to_AwayOrPresent>not able to set thermostat ${thermostat} to ${mode} mode (exception $e)")
	}

}

private def getSensorTempForAverage(indiceRoom, typeSensor='tempSensor') {
	def key 
	def currentTemp=null
    	    
	if (typeSensor == 'tempSensor') {
		key = "tempSensor$indiceRoom"
	} else {
		key = "roomTstat$indiceRoom"
	}
	def tempSensor = settings[key]
	if (tempSensor != null) {
		// do a refresh to get the latest temp value
		try {        
			tempSensor.refresh()
		} catch (e) {
			log.debug("getSensorTempForAverage>not able to do a refresh() on $tempSensor")
		}        
		log.debug("getTempSensorForAverage>found sensor ${tempSensor}")
		currentTemp = tempSensor.currentTemperature.toFloat().round(1)
	}
	return currentTemp
}

private def setRoomTstatSettings(indiceZone, indiceRoom) {

	def scale = getTemperatureScale()
	float desiredHeat, desiredCool
	Boolean setClimate = false
	def key = "zoneName$indiceZone"
	def zoneName = settings[key]

	key = "givenClimate$indiceZone"
	def climateName = settings[key]

	key = "roomTstat$indiceRoom"
	def roomTstat = settings[key]

	key = "roomName$indiceRoom"
	def roomName = settings[key]

	log.debug("ecobeeSetZoneWithSchedule>in room ${roomName},about to apply zone's temp settings at ${roomTstat}")
	String mode = thermostat?.currentThermostatMode.toString() // get the mode at the main thermostat
	if (mode == 'heat') {
		roomTstat.heat()
		if ((climateName != null) && (climateName.trim() != "")) {
			try {
				roomTstat?.setClimate("", climateName)
				setClimate = true
			} catch (any) {
				log.debug("setRoomTstatSettings>in room ${roomName},not able to set climate ${climateName} for heating at the thermostat ${roomTstat}")

			}
		}
		if (!setClimate) {
			log.debug("ecobeeSetZoneWithSchedule>in room ${roomName},about to apply zone's temp settings")
			key = "desiredHeatTemp$indiceZone"
			def heatTemp = settings[key]
			if ((heatTemp == null) || (heatTemp?.trim()=="")) {
				log.debug("setRoomTstatSettings>in room ${roomName},about to apply default heat settings")
				desiredHeat = (scale=='C') ? 21:72				// by default, 21°C/72°F is the target heat temp
			} else {
				desiredHeat = heatTemp.toFloat()
			}
			log.debug("setRoomTstatSettings>in room ${roomName},${roomTstat}'s desiredHeat=${desiredHeat}")
			roomTstat.setHeatingSetpoint(desiredHeat)
			send("ecobeeSetZoneWithSchedule>in room ${roomName}, ${roomTstat}'s heating setPoint now =${desiredHeat}°")
		}
	} else if (mode == 'cool') {

		roomTstat.cool()
		if ((climateName != null) && (climateName.trim() != "")) {
			try {
				roomTstat?.setClimate("", climateName)
				setClimate = true
			} catch (any) {
				log.debug("setRoomTstatSettings>in room ${roomName},not able to set climate ${climateName} for cooling at the thermostat ${roomTstat}")

			}
		}
		if (!setClimate) {
			log.debug("ecobeeSetZoneWithSchedule>in room ${roomName},about to apply zone's temp settings")
			key = "desiredCoolTemp$indiceZone"
			def coolTemp = settings[key]
			if ((coolTemp == null) || (coolTemp?.trim()=="")) {
				log.debug("setRoomTstatSettings>in room ${roomName},about to apply default cool settings")
				desiredCool = (scale=='C') ? 23:75				// by default, 23°C/75°F is the target cool temp
			} else {
            
				desiredCool = coolTemp.toFloat()
			}
			log.debug("setRoomTstatSettings>in room ${roomName}, ${roomTstat}'s desiredCool=${desiredCool}")
			roomTstat.setCoolingSetpoint(desiredCool)
			send("ecobeeSetZoneWithSchedule>in room ${roomName}, ${roomTstat}'s cooling setPoint now =${desiredCool}°")
		}
	}
}

private def setAllRoomTstatsSettings(indiceZone) {
	def foundRoomTstat = false

	def key = "includedRooms$indiceZone"
	def rooms = settings[key]
	for (room in rooms) {

		def roomDetails=room.split(':')
		def indiceRoom = roomDetails[0]
		def roomName = roomDetails[1]

		key = "needOccupiedFlag$indiceRoom"
		def needOccupied = (settings[key]) ?: 'false'
		key = "roomTstat$indiceRoom"
		def roomTstat = settings[key]

		if (!roomTstat) {
			continue
		}
		log.debug("setAllRoomTstatsSettings>found a room Tstat ${roomTstat}, needOccupied=${needOccupied} in room ${roomName}, indiceRoom=${indiceRoom}")
		foundRoomTstat = true
		if (needOccupied == 'true') {

			key = "motionSensor$indiceRoom"
			def motionSensor = settings[key]
			if (motionSensor != null) {

				if (isRoomOccupied(motionSensor, indiceRoom)) {
					log.debug("setAllRoomTstatsSettings>for occupied room ${roomName},about to call setRoomTstatSettings ")
					setRoomTstatSettings(indiceZone, indiceRoom)
				} else {
                
					log.debug("setAllRoomTstatsSettings>room ${roomName} not occupied,skipping it")
                
				}
			}
		} else {

			log.debug("setAllRoomTstatsSettings>for room ${roomName},about to call setRoomTstatSettings ")
			setRoomTstatSettings(indiceZone, indiceRoom)
		}
	}
	return foundRoomTstat
}

private def getAllTempsForAverage(indiceZone) {
	def tempAtSensor

	def indoorTemps = []
	def key = "includedRooms$indiceZone"
	def rooms = settings[key]
	for (room in rooms) {

		def roomDetails=room.split(':')
		def indiceRoom = roomDetails[0]
		def roomName = roomDetails[1]

		key = "needOccupiedFlag$indiceRoom"
		def needOccupied = (settings[key]) ?: 'false'
		log.debug("getAllTempsForAverage>looping thru all rooms,now room=${roomName},indiceRoom=${indiceRoom}, needOccupied=${needOccupied}")

		if (needOccupied == 'true') {

			key = "motionSensor$indiceRoom"
			def motionSensor = settings[key]
			if (motionSensor != null) {

				if (isRoomOccupied(motionSensor, indiceRoom)) {

					tempAtSensor = getSensorTempForAverage(indiceRoom)
					if (tempAtSensor != null) {
						indoorTemps = indoorTemps + tempAtSensor.toFloat().round(1)
						log.debug("getAllTempsForAverage>added ${tempAtSensor.toString()} due to occupied room ${roomName} based on ${motionSensor}")
					}
					tempAtSensor = getSensorTempForAverage(indiceRoom,'roomTstat')
					if (tempAtSensor != null) {
						indoorTemps = indoorTemps + tempAtSensor.toFloat().round(1)
						log.debug("getAllTempsForAverage>added ${tempAtSensor.toString()} due to occupied room ${roomName} based on ${motionSensor}")
					}
				}
			}

		} else {

			tempAtSensor = getSensorTempForAverage(indiceRoom)
			if (tempAtSensor != null) {
				log.debug("getAllTempsForAverage>added ${tempAtSensor.toString()} in room ${roomName}")
				indoorTemps = indoorTemps + tempAtSensor.toFloat().round(1)
			}
			tempAtSensor = getSensorTempForAverage(indiceRoom,'roomTstat')
			if (tempAtSensor != null) {
				indoorTemps = indoorTemps + tempAtSensor.toFloat().round(1)
 				log.debug("getAllTempsForAverage>added ${tempAtSensor.toString()} in room ${roomName}")
			}

		}
	} /* end for */
	return indoorTemps

}

private def set_fan_mode(indiceSchedule) {

	def key = "fanMode$indiceSchedule"
	def fanMode = settings[key]
	key = "scheduleName$indiceSchedule"
	def scheduleName = settings[key]
    
    
    
	if (fanMode == null) {
		return     
	}

	key = "fanModeForThresholdOnlyFlag${indiceSchedule}"
	def fanModeForThresholdOnlyFlag = settings[key]

	def fanModForThresholdOnly = (fanModeForThresholdOnlyFlag) ?: 'false'
	if (fanModForThresholdOnly=='true') {
    
		if (outTempSensor == null) {
			return     
		}

		key = "moreFanThreshold$indiceSchedule"
		def moreFanThreshold = settings[key]
		log.debug("setFanMode>fanModeForThresholdOnly=$fanModForThresholdOnly,morefanThreshold=$moreFanThreshold")
		if (moreFanThreshold == null) {
			return     
		}
		// do a refresh to get latest temp value
		try {        
			outTempSensor.refresh()
		} catch (e) {
			log.debug("setFanMode>not able to do a refresh() on $outTempSensor")
		}
		float outdoorTemp = outTempSensor?.currentTemperature.toFloat().round(1)
        
		if (outdoorTemp < moreFanThreshold.toFloat()) {
			fanMode='off'	// fan mode should be set then at 'off'			
		}
		if (detailedNotif == 'true') {
			send("ecobeeSetZoneWithSchedule>schedule ${scheduleName},outdoorTemp=$outdoorTemp, about to set fan mode to ${fanMode} at thermostat ${thermostat} as requested")
		}
	}    

	try {
		thermostat?.setThermostatFanMode(fanMode)
		if (detailedNotif == 'true') {
			send("ecobeeSetZoneWithSchedule>schedule ${scheduleName},set fan mode to ${fanMode} at thermostat ${thermostat} as requested")
		}
	} catch (e) {
		log.debug("set_fan_mode>schedule ${scheduleName},not able to set fan mode to ${fanMode} (exception $e) at thermostat ${thermostat}")
	}
}


private def adjust_tstat_for_more_less_heat_cool(indiceSchedule) {
	def scale = getTemperatureScale()
	def key = "setRoomThermostatsOnlyFlag$indiceSchedule"
	def setRoomThermostatsOnlyFlag = settings[key]
	def setRoomThermostatsOnly = (setRoomThermostatsOnlyFlag) ?: 'false'

	key = "scheduleName$indiceSchedule"
	def scheduleName = settings[key]

	if (setRoomThermostatsOnly=='true') {
		log.debug("adjust_tstat_for_more_less_heat_cool>schedule ${scheduleName},all room Tstats set and setRoomThermostatsOnlyFlag= true,exiting")
		return				    
	}    

	if (outTempSensor == null) {
		log.debug "adjust_tstat_for_more_less_heat_cool>no outdoor temp sensor set, exiting"
		return     
	}
	
	key = "moreHeatThreshold$indiceSchedule"
	def moreHeatThreshold = settings[key]
	key = "moreCoolThreshold$indiceSchedule"
	def moreCoolThreshold = settings[key]
	key = "lessHeatThreshold$indiceSchedule"
	def lessHeatThreshold = settings[key]
	key = "lessCoolThreshold$indiceSchedule"
	def lessCoolThreshold = settings[key]
	
	if ((moreHeatThreshold == null) && (moreCoolThreshold ==null) && 
		(lessHeatThreshold  == null) && (lessCoolThreshold ==null)) {
		log.debug "adjust_tstat_for_more_less_heat_cool>no adjustment variables set, exiting"
		return
	}
	
	// do a refresh to get latest temp value
	try {        
		outTempSensor.refresh()
	} catch (e) {
		log.debug("setFanMode>not able to do a refresh() on $outTempSensor")
	}
	float outdoorTemp = outTempSensor?.currentTemperature.toFloat().round(1)
    
	String currentMode = thermostat.currentThermostatMode.toString()
	float currentHeatPoint = thermostat.currentHeatingSetpoint.toFloat().round(1)
	float currentCoolPoint = thermostat.currentCoolingSetpoint.toFloat().round(1)
	float currentScheduleHeat = thermostat.currentProgramHeatTemp.toFloat().round(1)
	float currentScheduleCool = thermostat.currentProgramCoolTemp.toFloat().round(1)
	float targetTstatTemp    
	log.debug "adjust_tstat_for_more_less_heat_cool>currentMode=$currentMode,outdoorTemp=$outdoorTemp,moreCoolThreshold=$moreCoolThreshold,  moreHeatThreshold=$moreHeatThreshold," +
		"coolModeThreshold=$coolModeThreshold,heatModeThreshold=$heatModeThreshold,currentHeatSetpoint=$currentHeatPoint,currentCoolSetpoint=$currentCoolPoint"

	key = "givenMaxTempDiff$indiceSchedule"
	def givenMaxTempDiff = settings[key]
	def input_max_temp_diff = givenMaxTempDiff ?: (scale=='C')? 2: 5 // 2°C/5°F temp differential is applied by default

	float max_temp_diff = input_max_temp_diff.toFloat().round(1)
    
	if (currentMode== 'heat') {
		if ((moreHeatThreshold != null) & (outdoorTemp <= moreHeatThreshold?.toFloat()))  {
			targetTstatTemp = (currentHeatPoint + max_temp_diff).round(1)
			float temp_diff = (state.scheduleHeatSetpoint   - targetTstatTemp).round(1)
			// if temp diff is <= max_temp_diff, then do the adjustment            
			log.debug "adjust_tstat_for_more_less_heat_cool>temp_diff=$temp_diff, max_temp_diff=$max_temp_diff for more heat" 
			if (temp_diff.abs() > max_temp_diff) {
				log.debug("adjust_tstat_for_more_less_heat_cool>schedule ${scheduleName}:max_temp_diff= ${max_temp_diff},temp_diff=${temp_diff},too much adjustment for more heat")
				targetTstatTemp = (currentScheduleHeat + max_temp_diff).round(1)
			}
			thermostat.setHeatingSetpoint(targetTstatTemp)
			send("ecobeeSetZoneWithSchedule>heating setPoint now= ${targetTstatTemp}°, outdoorTemp <=${moreHeatThreshold}°")
            
		} else if ((lessHeatThreshold != null) && (outdoorTemp > lessHeatThreshold?.toFloat()))  {
			targetTstatTemp = (currentHeatPoint - max_temp_diff).round(1)
			float temp_diff = (state.scheduleHeatSetpoint   - targetTstatTemp).round(1)
			// if temp diff is <= max_temp_diff, then do the adjustment            
			log.debug "adjust_tstat_for_more_less_heat_cool>temp_diff=$temp_diff, max_temp_diff=$max_temp_diff for less leat" 
			if (temp_diff.abs() > max_temp_diff) {
				log.debug("adjust_tstat_for_more_less_heat_cool>schedule ${scheduleName}:max_temp_diff= ${max_temp_diff},temp_diff=${temp_diff},too much adjustment for less heat")
				targetTstatTemp = (currentScheduleHeat - max_temp_diff).round(1)
			}
			thermostat.setHeatingSetpoint(targetTstatTemp)
			send("ecobeeSetZoneWithSchedule>heating setPoint now= ${targetTstatTemp}°, outdoorTemp > ${lessHeatThreshold}°")
		}            
	}
	if (currentMode== 'cool') {
    
		if ((moreCoolThreshold!= null) && (outdoorTemp >= moreCoolThreshold?.toFloat())) {
			targetTstatTemp = (currentCoolPoint - max_temp_diff).round(1)
			float temp_diff =  (state.scheduleCoolSetpoint - targetTstatTemp).round(1)
			log.debug "adjust_tstat_for_more_less_heat_cool>temp_diff=$temp_diff, max_temp_diff=$max_temp_diff for more cool" 
			if (temp_diff.abs()  > max_temp_diff) {
				log.debug("adjust_tstat_for_more_less_heat_cool>schedule ${scheduleName}:max_temp_diff= ${max_temp_diff},temp_diff=${temp_diff},too much adjustment for more cool")
				targetTstatTemp = (currentScheduleCool - max_temp_diff).round(1)
			}
			thermostat.setCoolingSetpoint(targetTstatTemp)
			send("ecobeeSetZoneWithSchedule>cooling setPoint now= ${targetTstatTemp}°, outdoorTemp >=${moreCoolThreshold}°")
		} else if ((lessCoolThreshold != null) && (outdoorTemp < lessCoolThreshold?.toFloat())) {
			targetTstatTemp = (currentCoolPoint + max_temp_diff).round(1)
			float temp_diff = (state.scheduleCoolSetpoint - targetTstatTemp).round(1)
			log.debug "adjust_tstat_for_more_less_heat_cool>temp_diff=$temp_diff, max_temp_diff=$max_temp_diff for less cool" 
			if (temp_diff.abs() > max_temp_diff) {
				log.debug("adjust_tstat_for_more_less_heat_cool>schedule ${scheduleName}:max_temp_diff= ${max_temp_diff},temp_diff=${temp_diff},too much adjustment for less cool")
				targetTstatTemp = (currentScheduleCool + max_temp_diff).round(1)
			}
			send("ecobeeSetZoneWithSchedule>cooling setPoint now= ${targetTstatTemp}°, outdoorTemp <${lessCoolThreshold}°")
			thermostat.setCoolingSetpoint(targetTstatTemp)
		}        
	} 
}


private def adjust_thermostat_setpoint_in_zone(indiceSchedule) {
	def scale = getTemperatureScale()
	float desiredHeat, desiredCool, avg_indoor_temp

	def key = "scheduleName$indiceSchedule"
	def scheduleName = settings[key]

	key = "includedZones$indiceSchedule"
	def zones = settings[key]
	key = "setRoomThermostatsOnlyFlag$indiceSchedule"
	def setRoomThermostatsOnlyFlag = settings[key]
	def setRoomThermostatsOnly = (setRoomThermostatsOnlyFlag) ?: 'false'
	def indoor_all_zones_temps=[]

	log.debug("adjust_thermostat_setpoint_in_zone>schedule ${scheduleName}: zones= ${zones}")

	for (zone in zones) {

		def zoneDetails=zone.split(':')
		log.debug("adjust_thermostat_setpoint_in_zone>zone=${zone}: zoneDetails= ${zoneDetails}")
		def indiceZone = zoneDetails[0]
		def zoneName = zoneDetails[1]
        
		log.debug("adjust_thermostat_setpoint_in_zone>schedule ${scheduleName}: looping thru all zones, now zoneName=${zoneName}, about to apply room Tstat's settings")
		setAllRoomTstatsSettings(indiceZone) 

		if (setRoomThermostatsOnly == 'true') { // Does not want to set the main thermostat, only the room ones

			if (detailedNotif == 'true') {
				send("ecobeeSetZoneWithSchedule>schedule ${scheduleName},zone ${zoneName}: all room Tstats set and setRoomThermostatsOnlyFlag= true, continue...")
			}
            
		} else {

			def indoorTemps = getAllTempsForAverage(indiceZone)
			indoor_all_zones_temps = indoor_all_zones_temps + indoorTemps
		}
	}
	if (setRoomThermostatsOnly=='true') {
		log.debug("adjust_thermostat_setpoint_in_zone>schedule ${scheduleName}: schedule ${scheduleName},all room Tstats set and setRoomThermostatsOnlyFlag= true,exiting")
		return				    
	}    
	//	Now will do an avg temp calculation based on all temp sensors to apply the desired temp settings at the main Tstat correctly

	float currentTemp = thermostat?.currentTemperature.toFloat().round(1)
	String mode = thermostat?.currentThermostatMode.toString()
	//	This is the avg indoor temp based on indoor temp sensors in all rooms in the zone
	log.debug("adjust_thermostat_setpoint_in_zone>schedule ${scheduleName},all temps collected from sensors=${indoor_all_zones_temps}")
	if (indoor_all_zones_temps != [] ) {
		avg_indoor_temp = (indoor_all_zones_temps.sum() / indoor_all_zones_temps.size()).round(1)
	} else {
		avg_indoor_temp = currentTemp
	}

	float temp_diff = (avg_indoor_temp - currentTemp).round(1)
	if (detailedNotif == 'true') {
		send("ecobeeSetZoneWithSchedule>schedule ${scheduleName}:avg temp= ${avg_indoor_temp},main Tstat's currentTemp= ${currentTemp},temp adjustment=${temp_diff.abs()}")
	}

	key = "givenMaxTempDiff$indiceSchedule"
	def givenMaxTempDiff = settings[key]
	def input_max_temp_diff = givenMaxTempDiff ?: (scale=='C')? 2: 5 // 2°C/5°F temp differential is applied by default

	float max_temp_diff = input_max_temp_diff.toFloat().round(1)
    
	key = "givenClimate$indiceSchedule"
	def climateName = settings[key]
	if (mode == 'heat') {
	
		desiredHeat = thermostat.currentHeatingSetpoint.toFloat().round(1)
		temp_diff = (temp_diff < (0-max_temp_diff)) ? max_temp_diff:(temp_diff >max_temp_diff) ?max_temp_diff:temp_diff // determine the temp_diff based on max_temp_diff
		log.debug("ecobeeSetZoneWithSchedule>schedule ${scheduleName}:max_temp_diff= ${max_temp_diff},temp_diff=${temp_diff} for heating")

		float targetTstatTemp = (desiredHeat - temp_diff).round(1)
		thermostat?.setHeatingSetpoint(targetTstatTemp)
		send("ecobeeSetZoneWithSchedule>schedule ${scheduleName},in zones=${zones},heating setPoint now =${targetTstatTemp}°,adjusted by avg temp diff (${temp_diff.abs()}°) between all temp sensors in zone")
		state.scheduleHeatSetpoint=targetTstatTemp // save the value for later processing in adjust_more_less_heat_cool()
        
	} else if (mode == 'cool') {

		desiredCool = thermostat.currentCoolingSetpoint.toFloat().round(1)
		temp_diff = (temp_diff <0-max_temp_diff)?max_temp_diff:(temp_diff >max_temp_diff)?max_temp_diff:temp_diff // determine the temp_diff based on max_temp_diff
		log.debug("ecobeeSetZoneWithSchedule>schedule ${scheduleName}:max_temp_diff= ${max_temp_diff},temp_diff=${temp_diff} for cooling")
		float targetTstatTemp = (desiredCool - temp_diff).round(1)
		thermostat?.setCoolingSetpoint(targetTstatTemp)
		send("ecobeeSetZoneWithSchedule>schedule ${scheduleName}, in zones=${zones},cooling setPoint now =${targetTstatTemp}°,adjusted by avg temp diff (${temp_diff}°) between all temp sensors in zone")
		state.scheduleCoolSetpoint=targetTstatTemp // save the value for later processing in adjust_more_less_heat_cool()
	}

}


private def adjust_vent_settings_in_zone(indiceSchedule) {
	float desiredTemp, avg_indoor_temp, avg_temp_diff

	def key = "scheduleName$indiceSchedule"
	def scheduleName = settings[key]

	key = "includedZones$indiceSchedule"
	def zones = settings[key]
	key = "setRoomThermostatsOnlyFlag$indiceSchedule"
	def setRoomThermostatsOnlyFlag = settings[key]
	def setRoomThermostatsOnly = (setRoomThermostatsOnlyFlag) ?: 'false'
	def indoor_all_zones_temps=[]
	def indiceRoom
	Boolean closedAllVentsInZone=true
	int nbVents=0
	def switchLevel    
    
	log.debug("adjust_vent_settings_in_zone>schedule ${scheduleName}: zones= ${zones}")

	if (setRoomThermostatsOnly=='true') {
		log.debug("adjust_vent_settings_in_zone>schedule ${scheduleName}: schedule ${scheduleName},all room Tstats set and setRoomThermostatsOnlyFlag= true,exiting")
		return				    
	}    
	String mode = thermostat?.currentThermostatMode.toString()
    
	if (mode=='heat') {
		desiredTemp = thermostat.currentHeatingSetpoint.toFloat().round(1)
	} else if (mode=='cool') {    
		desiredTemp = thermostat.currentCoolingSetpoint.toFloat().round(1)
    } else {
		desiredTemp = thermostat.currentThermostatSetpoint.toFloat().round(1)
	}    
	log.debug("adjust_vent_settings_in_zone>schedule ${scheduleName}, desiredTemp=${desiredTemp}")
    
	for (zone in zones) {

		def zoneDetails=zone.split(':')
		log.debug("adjust_vent_settings_in_zone>zone=${zone}: zoneDetails= ${zoneDetails}")
		def indiceZone = zoneDetails[0]
		def zoneName = zoneDetails[1]
		def indoorTemps = getAllTempsForAverage(indiceZone)

		if (indoorTemps != [] ) {
			avg_indoor_temp = (indoorTemps.sum() / indoorTemps.size()).round(1)
			avg_temp_diff = (avg_indoor_temp - desiredTemp).round(1)
			            
		} else {
			log.debug("adjust_vent_settings_in_zone>schedule ${scheduleName}, in zone ${zoneName}, no data from temp sensors, exiting")
		}        
		if (detailedNotif == 'true') {
			log.debug("adjust_vent_settings_in_zone>schedule ${scheduleName}, in zone ${zoneName}, avg_temp_diff=${avg_temp_diff}, all temps collected from sensors=${indoorTemps}")
		}
		key = "includedRooms$indiceZone"
		def rooms = settings[key]
		for (room in rooms) {
			def roomDetails=room.split(':')
			indiceRoom = roomDetails[0]
			def roomName = roomDetails[1]
			if ((roomName == null) || (roomName.trim() == "")) {
				continue
			}
			def tempAtSensor =getSensorTempForAverage(indiceRoom)			
			if (tempAtSensor != null) {
				float temp_diff_at_sensor = tempAtSensor.toFloat().round(1) - desiredTemp 
				log.debug("adjust_vent_settings_in_zone>schedule ${scheduleName}, in zone ${zoneName}, room ${roomName}, temp_diff_at_sensor=${temp_diff_at_sensor}, avg_temp_diff=${avg_temp_diff}")
				switchLevel = ((temp_diff_at_sensor / avg_temp_diff) * 100).round()
                                
				if ((mode == 'cool') && (temp_diff_at_sensor < avg_temp_diff) ) {
					switchLevel = 100-switchLevel 
				}                    
				if ((mode=='heat')  && (temp_diff_at_sensor > avg_temp_diff)) {
					switchLevel = 100-switchLevel 
				}                
				switchLevel =( switchLevel >=0)?((switchLevel<100)? switchLevel: 100):(switchlevel< (-100))?0:100+switchLevel
			} else {
            	// no Temp sensor in the room, then just open the vents at 50%
				switchLevel=50		            
			}              
			if (switchLevel >=10) {	
				closedAllVentsInZone=false
			}              
                
			for (int j = 1;(j <= 5); j++)  {
				key = "ventSwitch${j}$indiceRoom"
				def ventSwitch = settings[key]
				if (ventSwitch != null) {
					setVentSwitchLevel(indiceRoom, ventSwitch, switchLevel)                
					log.debug "adjust_vent_settings_in_zone>in zone=${zoneName},room ${roomName},set ${ventSwitch} at switchLevel =${switchLevel}%"
					nbVents++                    
				}
			} /* end for ventSwitch */                    
         
		} /* end for rooms */
		
	} /* end for zones */

	if (closedAllVentsInZone) {
    
		if (nbVents > 2) {        
			switchLevel=10        
			control_vent_switches_in_zone(indiceSchedule, switchLevel)		    
		} else {
			switchLevel=25        
			control_vent_switches_in_zone(indiceSchedule, switchLevel)		    
	        
		}
		log.debug "adjust_vent_settings_in_zone>schedule ${scheduleName}, set all ventSwitches at ${switchLevel}% to avoid closing all of them"
		if (detailedNotif == 'true') {
			send("ecobeeSetZoneWithSchedule>schedule ${scheduleName},set all ventSwitches at ${switchLevel}% to avoid closing all of them")
		}
	}    
}

private def turn_off_all_other_vents(ventSwitchesOnSet) {
	def foundVentSwitch
    
	for (indiceZone in 1..zonesCount) {
		def key = "zoneName$indiceZone"  
		def zoneName = settings[key]
    
		if ((zoneName == null) || (zoneName.trim() == "")) {
        
			continue
		}

		key = "includedRooms$indiceZone"
		def rooms = settings[key]
		for (room in rooms) {
			def roomDetails=room.split(':')
			def indiceRoom = roomDetails[0]
			def roomName = roomDetails[1]
			if ((roomName == null) || (roomName.trim() == "")) {
				continue
			}

			for (int j = 1;(j <= 5); j++)  {
	                
				key = "ventSwitch${j}$indiceRoom"
				def ventSwitch = settings[key]
				if (ventSwitch != null) {
					log.debug "turn_off_all_other_vents>in zone=${zoneName},room ${roomName},found=${ventSwitch}"
					foundVentSwitch = ventSwitchesOnSet.find{it == ventSwitch}
					if (foundVentSwitch ==null) {
						ventSwitch.off()
						log.debug("turn_off_all_other_vents>in zone ${zoneName},turned off ${ventSwitch} in room ${roomName} as requested to create the desired zone(s)")
					}                
				}
			} /* end for ventSwitch */                    

		}  /* end for rooms */          
	} /* end for zones */

}


private def setVentSwitchLevel(indiceRoom, ventSwitch, switchLevel=100) {

	def key = "roomName$indiceRoom"
	def roomName = settings[key]

	try {
		ventSwitch.setLevel(switchLevel)
		log.debug("setVentSwitchLevel>set ${ventSwitch} at level ${switchLevel} in room ${roomName} to reach desired temperature")
	} catch (e) {
		if (switchLevel >0) {
			ventSwitch.on()        
			log.error "setVentSwitchLevel>in room ${roomName}, not able to set ${ventSwitch} at ${switchLevel} (exception $e), trying to turn it on"
		} else {
			ventSwitch.off()        
			log.error "setVentSwitchLevel>in room ${roomName}, not able to set ${ventSwitch} at ${switchLevel} (exception $e), trying to turn it off"
		}
	}
    
}



private def control_vent_switches_in_zone(indiceSchedule, switchLevel=100) {

	def key = "includedZones$indiceSchedule"
	def zones = settings[key]
	def ventSwitchesOnSet=[]
    
	for (zone in zones) {

		def zoneDetails=zone.split(':')
		def indiceZone = zoneDetails[0]
		def zoneName = zoneDetails[1]
		key = "includedRooms$indiceZone"
		def rooms = settings[key]
    
		for (room in rooms) {
			def roomDetails=room.split(':')
			def indiceRoom = roomDetails[0]
			def roomName = roomDetails[1]


			for (int j = 1;(j <= 5); j++)  {
	                
				key = "ventSwitch${j}$indiceRoom"
				def ventSwitch = settings[key]
				if (ventSwitch != null) {
					ventSwitchesOnSet.add(ventSwitch)
					setVentSwitchLevel(indiceRoom, ventSwitch, switchLevel)
				}
			} /* end for ventSwitch */
		} /* end for rooms */
	} /* end for zones */
	return ventSwitchesOnSet
}



private send(msg) {
	if (sendPushMessage != "No") {
		sendPush(msg)
	}

	if (phone) {
		log.debug("sending text message")
		sendSms(phone, msg)
	}
	log.debug msg
}
