package com.nju.cloudnative_final.limit;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @program: CloudNative_final
 * @description:
 * @author: Gxy-2001
 * @create: 2021-07-14
 */

@ResponseStatus(value = HttpStatus.TOO_MANY_REQUESTS)
public class LimitException extends Exception {

}
