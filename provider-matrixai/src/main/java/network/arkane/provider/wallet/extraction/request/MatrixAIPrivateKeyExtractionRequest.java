package network.arkane.provider.wallet.extraction.request;

import lombok.Getter;
import network.arkane.provider.chain.SecretType;

@Getter
public class MatrixAIPrivateKeyExtractionRequest extends ExtractionRequest {
    private String privateKey;

    public MatrixAIPrivateKeyExtractionRequest(final String privateKey) {
        super(SecretType.MATRIXAI);
        this.privateKey = privateKey;
    }
}
