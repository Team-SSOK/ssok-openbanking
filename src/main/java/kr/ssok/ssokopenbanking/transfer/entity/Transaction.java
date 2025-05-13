package kr.ssok.ssokopenbanking.transfer.entity;

import jakarta.persistence.*;
import kr.ssok.ssokopenbanking.transfer.dto.request.TransferRequestDto;
import kr.ssok.ssokopenbanking.transfer.enums.TransactionStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class Transaction {

    @Id
    private UUID transactionId;

    private String sendAccount;
    private String recvAccount;
    private int sendBankCode;
    private int recvBankCode;
    private Long amount;
    private String sendName;
    private String recvName;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // UUID 외부 주입
    public static Transaction create(TransferRequestDto dto, UUID txId) {
        LocalDateTime now = LocalDateTime.now();

        return Transaction.builder()
                .transactionId(txId)
                .sendAccount(dto.getSendAccountNumber())
                .recvAccount(dto.getRecvAccountNumber())
                .sendBankCode(dto.getSendBankCode())
                .recvBankCode(dto.getRecvBankCode())
                .amount(dto.getAmount())
                .sendName(dto.getSendName())
                .recvName(dto.getRecvName())
                .status(TransactionStatus.REQUESTED)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    // Transaction 상태 업데이트 메서드
    public void updateStatus(TransactionStatus newStatus) {
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }
}
