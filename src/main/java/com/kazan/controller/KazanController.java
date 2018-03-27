package com.kazan.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.kazan.model.Alert;
import com.kazan.model.KazanObject;
import com.kazan.repository.AlertRepository;
import com.kazan.repository.ObjectRepository;
import com.kazan.utils.KazanStringUtils;
import com.kazan.wrapper.AlertRequestWrapper;
import com.kazan.wrapper.ObjectWrapper;
import com.kazan.wrapper.ObjectRequestWrapper;

@RestController    
@RequestMapping(path="/kazan")
public class KazanController {
	
	@Autowired
	private ObjectRepository objectRepository;
	
	@Autowired
	private AlertRepository alertRepository;
	
	@RequestMapping(path="/alert/all")
	public @ResponseBody List<Alert> getAllAlerts() {
		return alertRepository.getAll();
	}
	
	@RequestMapping(method=RequestMethod.POST, path="/alert/add")
	public @ResponseBody String addAlert(@RequestBody AlertRequestWrapper gsonAlert) {
		Alert newAlert = new Alert();
		newAlert.setActionTime(new Date());
		newAlert.setContent(gsonAlert.getContent());
		newAlert.setImageUrl(gsonAlert.getImage_url());
		newAlert.setAlertType(gsonAlert.getTYPE());
		newAlert.setGroupId(-1);
		newAlert.setUserId(-1);
		alertRepository.add(newAlert);
		return gsonAlert.getContent();
	}
	
	@RequestMapping(path="/object/all")
	public @ResponseBody Iterable<KazanObject> getAllObjects() {
		return objectRepository.getAll();
	}
	
	@RequestMapping(path="/object/symbol")
	public @ResponseBody List<KazanObject> getObjectsBySymbol(@RequestParam String symbol) {
		return objectRepository.getBySymbol(symbol);
	}
	
	@RequestMapping(method=RequestMethod.POST, path="/object/add")
	public @ResponseBody List<KazanObject> addObject(@RequestBody ObjectRequestWrapper gsonWrapperObject) {
		objectRepository.delete(gsonWrapperObject.getSymbol());
		List<ObjectWrapper> objects = gsonWrapperObject.getObjects();
		for (ObjectWrapper ow : objects) {
			KazanObject ko = new KazanObject(ow.getSymbol(), ow.getObjprop_type(), 
											KazanStringUtils.formatDate(ow.getObjprop_time1()), KazanStringUtils.formatDate(ow.getObjprop_time2()),
											ow.getObjprop_price1(), ow.getObjprop_price2(), ow.getObjprop_width(), ow.getObjprop_color(), ow.getObjprop_scale());
			objectRepository.add(ko);
		}
		return null;
	}
	
	@GetMapping(path="/object/delete")
	public @ResponseBody int deleteObject(@RequestParam String symbol) {		
		return objectRepository.delete(symbol);
	}
}