package kr.ssok.ssokopenbanking.opensearch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/alerts")
public class TestAlertController {
    @PostMapping
    public ResponseEntity<Void> receiveAlert(@RequestBody Map<String, String> body) {
        String message = body.get("message");
        log.warn("[ALERT] {}", message);
        return ResponseEntity.ok().build();
    }
}