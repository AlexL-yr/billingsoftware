import { useContext, useState,useEffect} from 'react'
import './CartSummary.css'
import { AppContext } from '../../context/AppContext'
import toast from 'react-hot-toast'
import { createStripeOrder } from '../../Service/PaymentService'
import { deleteOrder, createOrder } from '../../Service/OrderService'
import { AppConstants } from '../../util/constants'
import ReceiptPopup from '../ReceiptPopup/ReceiptPopup.jsx'
const CartSummary = ({ customerName, mobileNumber, setMobileNumber, setCustomerName}) => {
  const { cartItems, clearCart, orderDetails,setOrderDetails } = useContext(AppContext);
  const [isProcessing, setIsProcessing] = useState(false)
  const [showPopup, setShowPopup] = useState(false)

  useEffect(() => {
  if (localStorage.getItem("stripePaid") === "true") {
    const savedOrder = JSON.parse(localStorage.getItem("lastOrder"))
    if (savedOrder) setOrderDetails(savedOrder)
    localStorage.removeItem("stripePaid")
    localStorage.removeItem("lastOrder")
  }
}, [])

  const totalAmount = cartItems.reduce((total, item) => total + item.price * item.quantity, 0)
  const tax = totalAmount * 0.01
  const grandTotal = totalAmount + tax
  const loadStripeScript = () => {
    return new Promise((resolve) => {
      const script = document.createElement("script")
      script.src = "https://js.stripe.com/v3/"
      script.onload = () => resolve(true)
      script.onerror = () => resolve(false)
      document.body.appendChild(script)
    })
  }

  const deleteOrderOnFailure = async (orderId) => {
    try {
      await deleteOrder(orderId)
    } catch (error) {
      console.error(error)
      toast.error("Something went wrong")
    }
  }

  const completePayment = async (paymentMode) => {

    if (!customerName || !mobileNumber) {
      toast.error("Please enter customer name and mobile number")
      return;
    }

    if (cartItems.length === 0) {
      toast.error("Please add items to the cart")
      return;
    }

    const orderData = {
      customerName,
      phoneNumber: mobileNumber,  
      cartItems,
      subtotal: totalAmount,
      tax,
      grandTotal,
      paymentMethod: paymentMode.toUpperCase()
    }

    setIsProcessing(true)

    try {
      const response = await createOrder(orderData)
      const savedData = response.data

      // Cash Pay
      if (response.status === 201 && paymentMode === "cash") {
        toast.success("Payment Successful")
        setOrderDetails(response.data)
        return
      }

      // Stripe
      if (response.status === 201 && paymentMode === "upi") {
        const stripeLoaded = await loadStripeScript()
        if (!stripeLoaded) {
          toast.error("Stripe script failed to load")
          await deleteOrderOnFailure(savedData.orderId)
          return
        }
        localStorage.setItem("lastOrder", JSON.stringify(response.data));
        localStorage.setItem("stripePaid", "true");
        localStorage.setItem("latestOrderId", response.data.orderId);

        const stripeResponse = await createStripeOrder({
          orderId: savedData.orderId,
          amount: Math.round(grandTotal),
          currency: "EUR"
        })

        if (!stripeResponse?.data?.id) {
          toast.error("Stripe order creation failed")
          await deleteOrderOnFailure(savedData.orderId)
          return
        }

        const stripe = window.Stripe(AppConstants.STRIPE_PUBLISHABLE_KEY)

        const result = await stripe.redirectToCheckout({
          sessionId: stripeResponse.data.id,
        })

        if (result.error) {
          await deleteOrderOnFailure(savedData.orderId)
          toast.error("Payment failed: " + result.error.message)
          return
        }
      }

    } catch (error) {
      console.error(error)
      toast.error("Something went wrong")
    } finally {
      setIsProcessing(false)
    }
  }
  const placeOrder = ()=>{
    setShowPopup(true)
  }
  const handlePrintReceipt = () => {
    window.print()
  }
  return (
    <div className="mt-2">

      <div className="cart-summary-details">
        <div className="d-flex justify-content-between mb-2">
          <span className="text-light">Item:</span>
          <span className="text-light">€{totalAmount.toFixed(2)}</span>
        </div>
        <div className="d-flex justify-content-between mb-2">
          <span className="text-light">Tax:</span>
          <span className="text-light">€{tax.toFixed(2)}</span>
        </div>
        <div className="d-flex justify-content-between mb-2">
          <span className="text-light">Total:</span>
          <span className="text-light">€{grandTotal.toFixed(2)}</span>
        </div>
      </div>

      <div className="d-flex gap-3">
        <button className="btn btn-success flex-grow-1"
          onClick={() => completePayment("cash")}
          disabled={isProcessing}
        >
          {isProcessing ? "Processing" : "Cash"}
        </button>

        <button className="btn btn-primary flex-grow-1"
          onClick={() => completePayment("upi")}
          disabled={isProcessing}
        >
          {isProcessing ? "Processing" : "UPI"}
        </button>
      </div>

      <div className="d-flex gap-3 mt-3">
        <button className="btn btn-warning flex-grow-1"
          disabled={!orderDetails}
          onClick={placeOrder}
        >
          Place Order
        </button>
        {
          showPopup && (
            <ReceiptPopup
              orderDetails={{
                ...orderDetails,
                stripeOrderId: orderDetails.paymentDetails?.stripeOrderId,
                stripePaymentId: orderDetails.paymentDetails?.stripePaymentId
              }}
              onClose={()=>setShowPopup(false)}
              onPrint={handlePrintReceipt}
            />
          )
        }
      </div>

    </div>
  )
}

export default CartSummary
