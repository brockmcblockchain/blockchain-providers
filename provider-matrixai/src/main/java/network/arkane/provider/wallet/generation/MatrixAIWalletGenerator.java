package network.arkane.provider.wallet.generation;

import network.arkane.provider.secret.generation.MatrixAISecretkey;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;

@Component
public class MatrixAIWalletGenerator implements WalletGenerator<MatrixAISecretkey> {

    @Override
    public GeneratedWallet generateWallet(final String password, final MatrixAISecretkey secret) {
        if (StringUtils.isEmpty(password)) {
            throw new IllegalArgumentException("Password should not be empty");
        }

        try {
            final WalletFile theWallet = Wallet.createStandard(password, secret.getKeyPair());
            return GeneratedMatrixAIWallet
                    .builder()
                    .walletFile(theWallet)
                    .address(getAddress(secret))
                    .build();
        } catch (CipherException e) {
            throw new IllegalArgumentException("Unable to generate a wallet from the provided keypair");
        }
    }

    private String getAddress(final MatrixAISecretkey matrixSecret) {
        throw new IllegalArgumentException("Not yet implemented");
    }
}
