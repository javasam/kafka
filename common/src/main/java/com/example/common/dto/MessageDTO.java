package com.example.common.dto;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;

import java.io.Serializable;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property="Client message", scope = MessageDTO.class)
public class MessageDTO implements Serializable {

    private String operations;
    private String bankAccountClientId;
    private String bankAccountTransferId;
    private String bankClientName;
    private String amount;
}
