package kr.ssok.ssokopenbanking.transfer.controller;

import kr.ssok.ssokopenbanking.global.response.ApiResponse;
import kr.ssok.ssokopenbanking.global.response.code.status.ErrorStatus;
import kr.ssok.ssokopenbanking.global.response.code.status.SuccessStatus;
import kr.ssok.ssokopenbanking.transfer.dto.request.TransferRequestDto;
import kr.ssok.ssokopenbanking.transfer.dto.response.TransferResponseDto;
import kr.ssok.ssokopenbanking.transfer.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// 송금 요청을 처리
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/openbank/transfers")
public class TransferController {

    private final TransferService transferService;


    @PostMapping
    public ResponseEntity<ApiResponse<TransferResponseDto>> transfer(@RequestBody TransferRequestDto dto) {
        TransferResponseDto result = transferService.processTransfer(dto);
        return ApiResponse.success(SuccessStatus.TRANSFER_SUCCESS, result).toResponseEntity();
    }
}
