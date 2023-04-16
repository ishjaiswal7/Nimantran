from flask import Flask, render_template
from flask import jsonify
from flask import request
import stripe

app = Flask(__name__)

pkey = 'pk_test_51MuvvMSFj6CNUSphKlV0Brxd9vz0UYOXdeAP3N70P5qxCsoBIji62SOD4hRs9R2e6aNLLWyOMDv857uAieAZyzYX00lOP8I4Wv'
skey = 'sk_test_51MuvvMSFj6CNUSphNOanun4LCgaCl952xdSt4eK2zhTW3F8PM1QGcvQoEKdWHQO62Z03XPM1tCgWRk1Spq9Rfl0300J6lGCVaY'

@app.route('/')
def index():
    return render_template('index.html')

@app.route('/checkout', methods=['GET','POST'])
def checkout():
    if request.method == 'POST':
        stripe.api_key = skey
        customer = stripe.Customer.create(email=request.form['email'])
        ephemeralKey = stripe.EphemeralKey.create(
            customer=customer['id'],
            stripe_version='2022-11-15',
        )
        paymentIntent = stripe.PaymentIntent.create(
            amount=int(float(request.form['amount'])),
            currency='inr',
            customer=customer['id'],
            automatic_payment_methods={
            'enabled': True,
            },
        )
        print(f"Log: {request.form.values()}")
        return jsonify(paymentIntent=paymentIntent.client_secret,
                        ephemeralKey=ephemeralKey.secret,
                        customer=customer.id,
                        publishableKey=pkey)
    return render_template('index.html')


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8000, debug=True)
 