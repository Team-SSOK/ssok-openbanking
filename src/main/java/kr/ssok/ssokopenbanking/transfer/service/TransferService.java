package kr.ssok.ssokopenbanking.transfer.service;

import kr.ssok.ssokopenbanking.transfer.dto.request.TransferRequestDto;
import kr.ssok.ssokopenbanking.transfer.dto.response.TransferResponseDto;

public interface TransferService {
    TransferResponseDto processTransfer(TransferRequestDto dto);
}
