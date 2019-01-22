package network.arkane.provider.bitcoin.wallet.generation;

import com.google.protobuf.ByteString;
import network.arkane.provider.JSONUtil;
import network.arkane.provider.bitcoin.BitcoinEnv;
import network.arkane.provider.bitcoin.secret.generation.BitcoinSecretGenerator;
import network.arkane.provider.bitcoin.secret.generation.BitcoinSecretKey;
import network.arkane.provider.sochain.domain.Network;
import org.apache.commons.codec.binary.Base64;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.crypto.EncryptedData;
import org.bitcoinj.crypto.KeyCrypterScrypt;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.wallet.Protos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BitcoinWalletGeneratorTest {

    private BitcoinWalletGenerator walletGenerator;

    @BeforeEach
    void setUp() {
        walletGenerator = new BitcoinWalletGenerator(new BitcoinEnv(Network.BTCTEST, TestNet3Params.get()));
    }

    @Test
    void generatesWallet() {

        GeneratedBitcoinWallet wallet = walletGenerator.generateWallet("flqksjfqklm",
                                                                       new BitcoinSecretGenerator().generate());

        assertThat(wallet.getAddress()).isNotBlank();
        assertThat(wallet.secretAsBase64()).isNotBlank();

    }

    @Test
    void walletCanBeDecrypted() {
        String password = "flqksjfqklm";

        BitcoinSecretKey secretKey = new BitcoinSecretGenerator().generate();

        GeneratedBitcoinWallet wallet = walletGenerator.generateWallet(password,
                                                                       secretKey);

        String secret = wallet.secretAsBase64();
        BitcoinKeystore ed = JSONUtil.fromJson(new String(Base64.decodeBase64(secret)), BitcoinKeystore.class);
        Protos.ScryptParameters params = Protos.ScryptParameters.newBuilder().setSalt(ByteString.copyFrom(Base64.decodeBase64(ed.getSalt()))).build();
        KeyCrypterScrypt crypter = new KeyCrypterScrypt(params);
        EncryptedData encryptedData = new EncryptedData(Base64.decodeBase64(ed.getInitialisationVector()), Base64.decodeBase64(ed.getEncryptedBytes()));
        ECKey ecKey = ECKey.fromEncrypted(encryptedData, crypter, Base64.decodeBase64(ed.getPubKey()));
        ECKey unencrypted = ecKey.decrypt(crypter.deriveKey(password));

        assertThat(unencrypted.getPrivateKeyAsHex()).isEqualTo(secretKey.getKey().getPrivateKeyAsHex());
    }

}