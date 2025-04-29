package kr.ssok.ssokopenbanking.authentication.repository;

import kr.ssok.ssokopenbanking.authentication.entity.ApiKey;
import org.springframework.data.repository.CrudRepository;

public interface ApiKeyRepository extends CrudRepository<ApiKey, String> {
}
