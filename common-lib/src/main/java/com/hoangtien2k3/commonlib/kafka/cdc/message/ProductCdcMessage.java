package com.hoangtien2k3.commonlib.kafka.cdc.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@lombok.Getter
@lombok.Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCdcMessage {

    private Product after;

    private Product before;

    private Operation op;

}

