package pdo.fraudshield.mapper;

import org.mapstruct.Mapper;
import pdo.fraudshield.entity.FraudAnalysis;
import pdo.fraudshield.entity.Transaction;
import pdo.fraudshield.dto.FraudAnalysisResponse;
import pdo.fraudshield.dto.TransactionRequest;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    Transaction toDomain(TransactionRequest request);

    FraudAnalysisResponse toResponse(FraudAnalysis analysis);
}
