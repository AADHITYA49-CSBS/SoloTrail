export interface Attraction{id:number;attractionName:string;city:string;category:string;rating:number;openNow:boolean;description:string}
export interface Hotel{id:number;hotelName:string;city:string;pricePerNight:number;rating:number;available:boolean;description:string}
export interface Restaurant{id:number;restaurantName:string;city:string;cuisine:string;priceRange:string;rating:number;openNow:boolean;description:string}
export interface TouristGuide{id:number;guideName:string;city:string;languages:string;experienceYears:number;pricePerDay:number;specialization:string;available:boolean;contactNumber:string}
export interface Trip{id?:number;source:string;destination:string;travelDate:string;budget:number;transportMode:string;userId?:number}
export interface AuthUser{id?:number;name?:string;email?:string}
