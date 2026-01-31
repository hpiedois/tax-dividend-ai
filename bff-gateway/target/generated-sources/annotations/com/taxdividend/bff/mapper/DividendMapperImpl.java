package com.taxdividend.bff.mapper;

import com.taxdividend.bff.client.model.Dividend;
import com.taxdividend.bff.model.DividendCase;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-31T12:03:05+0100",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.2 (Homebrew)"
)
@Component
public class DividendMapperImpl implements DividendMapper {

    @Override
    public DividendCase toDividendCase(Dividend source) {
        if ( source == null ) {
            return null;
        }

        DividendCase dividendCase = new DividendCase();

        dividendCase.setDate( source.getPaymentDate() );
        dividendCase.setSecurity( source.getSecurityName() );
        dividendCase.setReclaimedAmount( source.getReclaimableAmount() );
        if ( source.getId() != null ) {
            dividendCase.setId( source.getId().toString() );
        }
        dividendCase.setGrossAmount( source.getGrossAmount() );

        dividendCase.setStatus( DividendCase.StatusEnum.PENDING );

        return dividendCase;
    }
}
