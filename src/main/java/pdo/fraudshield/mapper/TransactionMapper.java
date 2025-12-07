package pdo.fraudshield.mapper;

import org.mapstruct.Mapper;
import pdo.fraudshield.entity.FraudAnalysis;
import pdo.fraudshield.entity.Transaction;
import pdo.fraudshield.dto.FraudAnalysisResponse;
import pdo.fraudshield.dto.TransactionRequest;

@Mapper(config = GlobalMapperConfig.class)
public interface TransactionMapper {

    Transaction toEntity(TransactionRequest request);

    FraudAnalysisResponse toResponse(FraudAnalysis analysis);
}
