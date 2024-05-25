package com.SmoothSailing.models;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import com.SmoothSailing.models.CompanyModel;
import java.util.UUID;

@Component
public class CompanyModelConverter implements Converter<String, CompanyModel> {
    @Override
    public CompanyModel convert(String companyId) {
        CompanyModel companyModel = new CompanyModel();
        companyModel.setId(companyId);
        return companyModel;
    }
}