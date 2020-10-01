package id.co.dapenbi.basicsetup.controller;

import id.co.dapenbi.basicsetup.finder.LookupDependentFinder;
import id.co.dapenbi.basicsetup.finder.LookupItemFinder;
import id.co.dapenbi.basicsetup.model.LookupDependent;
import id.co.dapenbi.basicsetup.model.LookupItem;
import id.co.dapenbi.basicsetup.service.LookupDependentService;
import id.co.dapenbi.basicsetup.service.LookupItemService;
import id.co.dapenbi.core.model.Privilege;
import id.co.dapenbi.core.util.MessageUtil;
import id.co.dapenbi.core.util.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/basicsetup/lookupDependent")
public class LookupDependentController {
    @Autowired
    LookupDependentService lookupDependentService;

    @Autowired
    LookupItemService lookupItemService;

    private static final Logger logger = LoggerFactory.getLogger(LookupDependentController.class);

    @RequestMapping("/{id}")
    public String index(Model model, @PathVariable("id") String id, HttpServletRequest request) {
        Privilege privilege = new Privilege();
        try {
            privilege = SecurityUtil.getUserPrivilege(SecurityUtil.getUsername(), "/basicsetup/lookup/inquiry");
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        model.addAttribute("id", id);
        model.addAttribute("flagRead", privilege.isFlagRead());
        model.addAttribute("flagEdit", privilege.isFlagEdit());
        model.addAttribute("flagDelete", privilege.isFlagDelete());

        return "basic-setup/lookup/dependent";
    }

    @RequestMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.POST, value = "/getLookupDependentDataTable")
    @ResponseBody
    public DataTablesOutput<LookupDependent> getLookupDependentDataTable(@Valid @RequestBody DataTablesInput input)
            throws ParseException {
        return lookupDependentService.findForDataTable(input);
    }

    @RequestMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.POST, value = "/getLookupDependent")
    public ResponseEntity<Map<String, Object>> getLookupDependent(@ModelAttribute LookupDependentFinder finder, HttpServletRequest request) throws ParseException {
        Map<String, Object> data = new HashMap<String, Object>();

        List<LookupDependent> lookupDependentList = lookupDependentService.findByCriteria(finder);

        data.put("error", false);
        data.put("data", lookupDependentList);

        return ResponseEntity.status(HttpStatus.OK).body(data);
    }

    @RequestMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.POST, value = "/save")
    public ResponseEntity<Map<String, Object>> save(@ModelAttribute LookupDependent lookupDependent, HttpServletRequest request) throws ParseException {
        Map<String, Object> data = new HashMap<String, Object>();

        LookupItemFinder finder = new LookupItemFinder();
        finder.setLookupItemId(Long.parseLong(request.getParameter("lookupItemId")));

        LookupItem lookupItem = lookupItemService.findByCriteria(finder).get(0);

        lookupDependent.setLookupItem(lookupItem);
        lookupDependent.setLastUpdateDate(new Date());
        lookupDependent.setLastUpdatedBy(SecurityUtil.getUsername());

        if(lookupDependent.getLookupDependentId() == null){
            lookupDependent.setCreatedBy(SecurityUtil.getUsername());
            lookupDependent.setCreationDate(new Date());
        }

        try {
            boolean result = lookupDependentService.save(lookupDependent);

            if (result) {
                data.put("error", false);
                data.put("data", lookupDependent);
            } else {
                data.put("error", true);
                data.put("message", MessageUtil.ERROR_INSERT_UPDATE_DATA);
            }

        } catch (Exception e) {
            data.put("error", true);
            data.put("message", MessageUtil.ERROR_INSERT_UPDATE_DATA);

            e.printStackTrace();
            logger.error(e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.OK).body(data);
    }

    @RequestMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.POST, value = "/delete")
    public ResponseEntity<Map<String, Object>> delete(@RequestBody LookupDependent lookupDependent, HttpServletRequest request) throws ParseException {
        Map<String, Object> data = new HashMap<String, Object>();

        try {
            boolean result = lookupDependentService.delete(lookupDependent);

            if (result) {
                data.put("error", false);
            } else {
                data.put("error", true);
                data.put("message", MessageUtil.ERROR_DELETE_DATA);
            }
        } catch (Exception e) {
            data.put("error", true);
            data.put("message", MessageUtil.ERROR_DELETE_DATA);

            e.printStackTrace();
            logger.error(e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.OK).body(data);
    }
}
