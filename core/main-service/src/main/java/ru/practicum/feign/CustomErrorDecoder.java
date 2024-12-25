package ru.practicum.feign;


import feign.Response;
import feign.codec.ErrorDecoder;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.InternalServerErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CustomErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        HttpStatus httpStatus = HttpStatus.resolve(response.status());
        log.error("Feign error: method {}, status {}, cause {}", methodKey, response.status(), response.reason());

        assert httpStatus != null;
        return switch (httpStatus) {
            case BAD_REQUEST -> new BadRequestException("Bad Request");
            case INTERNAL_SERVER_ERROR -> new InternalServerErrorException("Internal Server Error");
            default -> defaultErrorDecoder.decode(methodKey, response);
        };
    }
}