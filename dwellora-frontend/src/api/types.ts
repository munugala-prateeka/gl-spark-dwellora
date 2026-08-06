// ---------- Shared ----------
export type Role = "PLATFORM_ADMIN" | "MANAGER" | "RESIDENT";
export type AccountStatus = "PENDING_ACTIVATION" | "ACTIVE" | "INACTIVE" | "SUSPENDED";

// ---------- Auth / User ----------
export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  userId: number;
  apartmentId: number | null;
  fullName: string;
  role: Role;
  email: string;
  token: string;
}

export interface ActivateAccountRequest {
  token: string;
  newPassword: string;
}

export interface UserResponse {
  userId: number;
  apartmentId: number | null;
  fullName: string;
  email: string;
  phone: string | null;
  flatNumber: string | null;
  role: Role;
  accountStatus: AccountStatus;
}

export interface ResidentRequest {
  apartmentId: number;
  fullName: string;
  email: string;
  phone: string;
  flatNumber: string;
}

export interface UserUpdateRequest {
  apartmentId: number;
  fullName: string;
  email: string;
  password?: string;
  phone: string;
  flatNumber: string;
}

// ---------- Onboarding ----------
export type OnboardingStatus = "PENDING" | "APPROVED" | "REJECTED";

export interface OnboardingRequest {
  apartmentName: string;
  address: string;
  city: string;
  state: string;
  pincode: string;
  totalBlocks: number;
  totalUnits: number;
  managerName: string;
  managerEmail: string;
  managerPhone: string;
}

export interface OnboardingResponse extends OnboardingRequest {
  requestId: number;
  status: OnboardingStatus;
  createdAt: string;
  approvedAt: string | null;
}

// ---------- Apartment ----------
export type ApartmentStatus = "PENDING_ACTIVATION" | "ACTIVE" | "SUSPENDED";

export interface ApartmentResponse {
  apartmentId: number;
  apartmentName: string;
  address: string;
  city: string;
  state: string;
  pincode: string;
  totalBlocks: number;
  totalUnits: number;
  status: ApartmentStatus;
}

// ---------- Amenity ----------
export type AmenityType =
  | "GYM"
  | "SWIMMING_POOL"
  | "COMMUNITY_HALL"
  | "BADMINTON_COURT"
  | "TENNIS_COURT"
  | "BASKETBALL_COURT"
  | "YOGA_STUDIO"
  | "CHILDRENS_PLAY_AREA"
  | "PARTY_HALL"
  | "LIBRARY";

export type BookingPolicy = "PER_PERSON" | "PER_FLAT";

export interface AmenityRequest {
  apartmentId: number;
  amenityName: string;
  amenityType: AmenityType;
  capacity: number;
  available: boolean;
  openingTime: string; // "HH:mm:ss"
  closingTime: string;
  bookingPolicy: BookingPolicy;
  slotDurationMinutes: number;
  maxBookingsPerDay: number;
  maxBookingsPerMonth?: number | null;
}

export interface AmenityResponse extends AmenityRequest {
  amenityId: number;
}

// ---------- Booking ----------
export type BookingStatus = "BOOKED" | "CANCELLED";

export interface BookingRequest {
  userId: number;
  amenityId: number;
  bookingDate: string; // "yyyy-MM-dd"
  startTime: string; // "HH:mm:ss"
  endTime: string;
}

export interface BookingResponse {
  bookingId: number;
  userId: number;
  amenityId: number;
  amenityName: string;
  bookingDate: string;
  startTime: string;
  endTime: string;
  bookingStatus: BookingStatus;
}

export interface AdminBookingResponse {
  bookingId: number;
  residentName: string;
  flatNumber: string;
  amenityName: string;
  bookingDate: string;
  startTime: string;
  endTime: string;
  bookingStatus: BookingStatus;
}

export interface AvailabilitySlot {
  slot: string;
  capacity: number;
  booked: number;
  remaining: number;
  bookingPolicy: string;
}

// ---------- Complaints ----------
export type ComplaintStatus = "OPEN" | "IN_PROGRESS" | "RESOLVED";

export interface ComplaintRequest {
  apartmentId: number;
  category: string;
  description: string;
}

export interface ComplaintUpdateRequest {
  status: ComplaintStatus;
  resolutionRemark?: string;
}

export interface ComplaintResponse {
  complaintId: number;
  apartmentId: number;
  userId: number;
  flatNumber: string;
  category: string;
  description: string;
  status: ComplaintStatus;
  resolutionRemark: string | null;
  raisedAt: string;
  resolvedAt: string | null;
}

// ---------- Notices ----------
export interface NoticeRequest {
  apartmentId: number;
  title: string;
  body: string;
  isUrgent: boolean;
  expiresAt?: string | null;
}

export interface NoticeResponse {
  noticeId: number;
  apartmentId: number;
  title: string;
  body: string;
  isUrgent: boolean;
  publishedAt: string;
  expiresAt: string | null;
}

// ---------- Events ----------
export interface EventRequest {
  apartmentId: number;
  title: string;
  description: string;
  eventDate: string; // ISO datetime
  capacity?: number | null;
}

export interface EventResponse {
  eventId: number;
  apartmentId: number;
  title: string;
  description: string;
  eventDate: string;
  capacity: number | null;
  currentRsvps: number;
  isFull: boolean;
}

// ---------- Notifications ----------
export type NotificationType = "BOOKING" | "COMPLAINT" | "NOTICE" | "ONBOARDING" | "EVENT";

export interface NotificationResponse {
  notificationId: number;
  userId: number;
  type: NotificationType;
  title: string;
  message: string;
  read: boolean;
  createdAt: string;
}
