package id.co.dapenbi.audittrail.service.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import id.co.dapenbi.audittrail.constant.ActionType;
import id.co.dapenbi.audittrail.model.AuditTrail;
import id.co.dapenbi.audittrail.repository.AuditTrailRepository;
import id.co.dapenbi.audittrail.service.AuditTrailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Repository
public class AuditTrailServiceImpl implements AuditTrailService {
    @Autowired
    AuditTrailRepository auditTrailRepository;

    private static final Logger logger = LoggerFactory.getLogger(AuditTrailServiceImpl.class);

    @Override
    public List<AuditTrail> findAll() {
        return null;
    }

    @Override
    public boolean doAuditCreate(String moduleCode, Object currentObject, String userId) {
        AuditTrail auditTrail = setDefaultAuditTrail(userId, ActionType.CREATE.getValue());
        auditTrail.setCurrentObject(objectToString(currentObject));
        auditTrail.setModuleCode(moduleCode);

        return saveAudit(auditTrail);
    }

    @Override
    public boolean doAuditUpdate(String moduleCode, Object currentObject, Object prevObject, String userId) {
        AuditTrail auditTrail = setDefaultAuditTrail(userId, ActionType.UPDATE.getValue());
        auditTrail.setCurrentObject(currentObject.toString());
        auditTrail.setPreviousObject(prevObject.toString());
        auditTrail.setModuleCode(moduleCode);

        return saveAudit(auditTrail);
    }

    @Override
    public boolean doAuditDelete(String moduleCode, Object prevObject, String userId) {
        AuditTrail auditTrail = setDefaultAuditTrail(userId, ActionType.DELETE.getValue());
        auditTrail.setPreviousObject(objectToString(prevObject));
        auditTrail.setModuleCode(moduleCode);

        return saveAudit(auditTrail);
    }

    @Override
    public boolean doAuditValidate(String moduleCode, Object prevObject, Object currentObject, String userId) {
        AuditTrail auditTrail = setDefaultAuditTrail(userId, ActionType.VALIDATE.getValue());
        auditTrail.setCurrentObject(currentObject.toString());
        auditTrail.setPreviousObject(prevObject.toString());
        auditTrail.setModuleCode(moduleCode);

        return saveAudit(auditTrail);
    }

    private AuditTrail setDefaultAuditTrail(String userId, String actionType){
        AuditTrail auditTrail = new AuditTrail();

        auditTrail.setActionType(actionType);
        auditTrail.setUserId(userId);
        auditTrail.setAuditTimestamp(new Date());

        return auditTrail;
    }

    private boolean saveAudit(AuditTrail auditTrail){
        try {
            auditTrailRepository.save(auditTrail);
        } catch (Exception e){
            e.printStackTrace();
            logger.error(e.getMessage());
            return false;
        }

        return true;
    }

    private String objectToString(Object obj) {
        Class<?> classObj = obj.getClass();
        Map<String, Object> values = new HashMap<>();

        try {
            for (Field field : classObj.getDeclaredFields()) {
                field.setAccessible(true);
                String fieldString = field.getName();

                values.put(fieldString, field.get(obj));
            }

        } catch (IllegalAccessException e){
            e.printStackTrace();
        }

        Gson gson = new Gson();
        Type gsonType = new TypeToken<HashMap>(){}.getType();
        String gsonString = gson.toJson(values, gsonType);

        return gsonString;
    }

}
